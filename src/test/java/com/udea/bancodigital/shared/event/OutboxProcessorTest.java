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

    @Mock
    private AesGcmCryptoUtil cryptoUtil;

    @InjectMocks
    private OutboxProcessor outboxProcessor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(outboxProcessor, "encryptionKey", "dummyKey");
    }

    @Test
    void processOutbox_Success_Encrypted() throws Exception {
        UUID eventId = UUID.randomUUID();
        String encryptedPayload = "encrypted-data";
        String plaintextPayload = "{\"test\":\"data\"}";

        Map<String, Object> eventRow = new HashMap<>();
        eventRow.put("id", eventId);
        eventRow.put("aggregate_id", "agg-1");
        eventRow.put("event_type", "TEST_TYPE");
        eventRow.put("payload", null);
        eventRow.put("encrypted_payload", encryptedPayload);
        eventRow.put("retry_count", 0);

        when(jdbcTemplate.queryForList(anyString(), any(Object[].class)))
                .thenReturn(Collections.singletonList(eventRow));
        when(cryptoUtil.decrypt(eq(encryptedPayload), anyString())).thenReturn(plaintextPayload);
        when(objectMapper.readValue(anyString(), eq(DomainEvent.class))).thenReturn(new DomainEvent() {
        });

        CompletableFuture<SendResult<String, DomainEvent>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(any(Message.class))).thenReturn(future);

        outboxProcessor.processOutbox();

        verify(kafkaTemplate).send(any(Message.class));
        verify(jdbcTemplate).update(contains("status = 'SENT'"), eq(eventId));
        verify(cryptoUtil).decrypt(eq(encryptedPayload), anyString());
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

        when(jdbcTemplate.queryForList(anyString(), any(Object[].class)))
                .thenReturn(Collections.singletonList(eventRow));
        when(cryptoUtil.encrypt(eq(payload), anyString())).thenReturn("encrypted-data");
        when(objectMapper.readValue(anyString(), eq(DomainEvent.class))).thenReturn(new DomainEvent() {
        });

        CompletableFuture<SendResult<String, DomainEvent>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(any(Message.class))).thenReturn(future);

        outboxProcessor.processOutbox();

        verify(cryptoUtil).encrypt(eq(payload), anyString());
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

        when(jdbcTemplate.queryForList(anyString(), any(Object[].class)))
                .thenReturn(Collections.singletonList(eventRow));
        when(cryptoUtil.encrypt(anyString(), anyString())).thenReturn("encrypted");
        when(objectMapper.readValue(anyString(), eq(DomainEvent.class))).thenReturn(new DomainEvent() {
        });

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
        eventRow.put("retry_count", 4);

        when(jdbcTemplate.queryForList(anyString(), any(Object[].class)))
                .thenReturn(Collections.singletonList(eventRow));
        when(cryptoUtil.encrypt(anyString(), anyString())).thenReturn("encrypted");
        when(objectMapper.readValue(anyString(), eq(DomainEvent.class))).thenReturn(new DomainEvent() {
        });

        CompletableFuture<SendResult<String, DomainEvent>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Final Error"));
        when(kafkaTemplate.send(any(Message.class))).thenReturn(future);

        outboxProcessor.processOutbox();

        verify(jdbcTemplate).update(contains("status = ?"), eq("DEAD_LETTER"), eq(5), any(), eq(eventId));
    }

    @Test
    void processOutbox_EmptyList_DoesNothing() {
        when(jdbcTemplate.queryForList(anyString(), any(Object[].class)))
                .thenReturn(Collections.emptyList());

        outboxProcessor.processOutbox();

        verifyNoInteractions(kafkaTemplate, objectMapper, cryptoUtil);
    }

    @Test
    void processOutbox_DecryptError_CallsHandleFailure() throws Exception {
        UUID eventId = UUID.randomUUID();

        Map<String, Object> eventRow = new HashMap<>();
        eventRow.put("id", eventId);
        eventRow.put("aggregate_id", "agg-1");
        eventRow.put("event_type", "TEST_TYPE");
        eventRow.put("payload", null);
        eventRow.put("encrypted_payload", "bad-encrypted");
        eventRow.put("retry_count", 0);

        when(jdbcTemplate.queryForList(anyString(), any(Object[].class)))
                .thenReturn(Collections.singletonList(eventRow));
        when(cryptoUtil.decrypt(anyString(), anyString())).thenThrow(new RuntimeException("Decryption failed"));

        outboxProcessor.processOutbox();

        verify(jdbcTemplate).update(contains("retry_count = ?"), eq("FAILED"), eq(1), anyString(), eq(eventId));
    }
}