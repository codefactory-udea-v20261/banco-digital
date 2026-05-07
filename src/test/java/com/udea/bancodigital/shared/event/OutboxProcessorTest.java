package com.udea.bancodigital.shared.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxProcessorTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OutboxProcessor outboxProcessor;

    private String validEncryptionKey;

    @BeforeEach
    void setUp() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        validEncryptionKey = Base64.getEncoder().encodeToString(key);
        ReflectionTestUtils.setField(outboxProcessor, "encryptionKey", validEncryptionKey);
    }

    @Test
    void processOutbox_Success_Encrypted() throws Exception {
        UUID eventId = UUID.randomUUID();
        String payload = "{\"test\":\"data\"}";
        String encryptedPayload = encrypt(payload, validEncryptionKey);

        Map<String, Object> eventRow = new HashMap<>();
        eventRow.put("id", eventId);
        eventRow.put("aggregate_id", "agg-1");
        eventRow.put("event_type", "TEST_TYPE");
        eventRow.put("payload", null);
        eventRow.put("encrypted_payload", encryptedPayload);
        eventRow.put("retry_count", 0);

        List<Map<String, Object>> resultList = Collections.singletonList(eventRow);
        // Cast to avoid ambiguous method call
        when(jdbcTemplate.queryForList(anyString(), any(Object[].class))).thenReturn(resultList);
        when(objectMapper.readValue(anyString(), eq(DomainEvent.class))).thenReturn(new DomainEvent() {});
        
        CompletableFuture<SendResult<String, DomainEvent>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(any(Message.class))).thenReturn(future);

        outboxProcessor.processOutbox();

        verify(kafkaTemplate).send(any(Message.class));
        verify(jdbcTemplate).update(contains("status = 'SENT'"), eq(eventId));
    }

    @Test
    void processOutbox_Success_Plaintext_MigratesToEncrypted() throws Exception {
        UUID eventId = UUID.randomUUID();
        String payload = "{\"test\":\"data\"}";

        Map<String, Object> eventRow = new HashMap<>();
        eventRow.put("id", eventId);
        eventRow.put("aggregate_id", "agg-1");
        eventRow.put("event_type", "TEST_TYPE");
        eventRow.put("payload", payload);
        eventRow.put("encrypted_payload", null);
        eventRow.put("retry_count", 0);

        List<Map<String, Object>> resultList = Collections.singletonList(eventRow);
        when(jdbcTemplate.queryForList(anyString(), any(Object[].class))).thenReturn(resultList);
        when(objectMapper.readValue(anyString(), eq(DomainEvent.class))).thenReturn(new DomainEvent() {});
        
        CompletableFuture<SendResult<String, DomainEvent>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(any(Message.class))).thenReturn(future);

        outboxProcessor.processOutbox();

        verify(jdbcTemplate).update(contains("SET encrypted_payload = ?"), any(), eq(eventId));
        verify(kafkaTemplate).send(any(Message.class));
    }

    @Test
    void processOutbox_KafkaError_IncrementsRetry() throws Exception {
        UUID eventId = UUID.randomUUID();
        String payload = "{\"test\":\"data\"}";

        Map<String, Object> eventRow = new HashMap<>();
        eventRow.put("id", eventId);
        eventRow.put("aggregate_id", "agg-1");
        eventRow.put("event_type", "TEST_TYPE");
        eventRow.put("payload", payload);
        eventRow.put("encrypted_payload", null);
        eventRow.put("retry_count", 0);

        List<Map<String, Object>> resultList = Collections.singletonList(eventRow);
        when(jdbcTemplate.queryForList(anyString(), any(Object[].class))).thenReturn(resultList);
        when(objectMapper.readValue(anyString(), eq(DomainEvent.class))).thenReturn(new DomainEvent() {});
        
        CompletableFuture<SendResult<String, DomainEvent>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka Down"));
        when(kafkaTemplate.send(any(Message.class))).thenReturn(future);

        outboxProcessor.processOutbox();

        verify(jdbcTemplate).update(contains("retry_count = ?"), eq("FAILED"), eq(1), eq("Kafka Down"), eq(eventId));
    }

    @Test
    void processOutbox_MaxRetriesReached_MovesToDLQ() throws Exception {
        UUID eventId = UUID.randomUUID();
        String payload = "{\"test\":\"data\"}";

        Map<String, Object> eventRow = new HashMap<>();
        eventRow.put("id", eventId);
        eventRow.put("aggregate_id", "agg-1");
        eventRow.put("event_type", "TEST_TYPE");
        eventRow.put("payload", payload);
        eventRow.put("encrypted_payload", null);
        eventRow.put("retry_count", 4); // Max is 5

        List<Map<String, Object>> resultList = Collections.singletonList(eventRow);
        when(jdbcTemplate.queryForList(anyString(), any(Object[].class))).thenReturn(resultList);
        when(objectMapper.readValue(anyString(), eq(DomainEvent.class))).thenReturn(new DomainEvent() {});
        
        CompletableFuture<SendResult<String, DomainEvent>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Final Error"));
        when(kafkaTemplate.send(any(Message.class))).thenReturn(future);

        outboxProcessor.processOutbox();

        verify(jdbcTemplate).update(contains("status = ?"), eq("DEAD_LETTER"), eq(5), any(), eq(eventId));
    }

    private String encrypt(String plaintext, String keyBase64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));
        byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
        return Base64.getEncoder().encodeToString(combined);
    }
}
