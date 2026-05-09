package com.udea.bancodigital.shared.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Processor for the Transactional Outbox Pattern with Retry and DLQ.
 * Periodically polls the outbox_events table for PENDING or FAILED events.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxProcessor {

    private final JdbcTemplate jdbcTemplate;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // FIX Sonar S2119: reusar SecureRandom como campo de clase
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${encryption.key}")
    private String encryptionKey;

    private static final String EVENTS_TOPIC = "banco-digital-events";
    private static final int MAX_RETRIES = 5;

    @Scheduled(fixedDelayString = "${outbox.poll.interval:2000}")
    public void processOutbox() {
        String sql = "SELECT id, aggregate_id, event_type, payload, encrypted_payload, retry_count FROM outbox_events "
                +
                "WHERE status IN ('PENDING', 'FAILED') AND retry_count < ? " +
                "ORDER BY created_at ASC LIMIT 100 FOR UPDATE SKIP LOCKED";

        List<Map<String, Object>> events = jdbcTemplate.queryForList(sql, MAX_RETRIES);

        for (Map<String, Object> eventRow : events) {
            String id = eventRow.get("id").toString();
            String aggregateId = (String) eventRow.get("aggregate_id");
            String eventType = (String) eventRow.get("event_type");
            String payload = (String) eventRow.get("payload");
            String encryptedPayload = (String) eventRow.get("encrypted_payload");
            int retryCount = (int) eventRow.get("retry_count");

            try {
                String plaintextPayload;
                if (encryptedPayload != null) {
                    plaintextPayload = decryptPayload(encryptedPayload);
                } else {
                    plaintextPayload = payload;
                    String encrypted = encryptPayload(payload);
                    jdbcTemplate.update("UPDATE outbox_events SET encrypted_payload = ? WHERE id = ?",
                            encrypted, java.util.UUID.fromString(id));
                }

                DomainEvent domainEvent = objectMapper.readValue(plaintextPayload, DomainEvent.class);

                Message<DomainEvent> message = MessageBuilder
                        .withPayload(domainEvent)
                        .setHeader(KafkaHeaders.TOPIC, EVENTS_TOPIC)
                        .setHeader(KafkaHeaders.KEY, aggregateId)
                        .setHeader("event_type", eventType)
                        .setHeader("event_id", id)
                        .build();

                kafkaTemplate.send(message).whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish outbox event {}: {}", id, ex.getMessage());
                        handleFailure(id, retryCount, ex.getMessage());
                    } else {
                        jdbcTemplate.update(
                                "UPDATE outbox_events SET status = 'SENT', updated_at = CURRENT_TIMESTAMP WHERE id = ?",
                                java.util.UUID.fromString(id));
                        log.debug("Outbox event {} published successfully", id);
                    }
                });

            } catch (Exception e) {
                log.error("Failed to process outbox event {}: {}", id, e.getMessage());
                handleFailure(id, retryCount, e.getMessage());
            }
        }
    }

    private void handleFailure(String id, int currentRetryCount, String errorMessage) {
        int nextRetry = currentRetryCount + 1;
        String status = nextRetry >= MAX_RETRIES ? "DEAD_LETTER" : "FAILED";

        jdbcTemplate.update(
                "UPDATE outbox_events SET status = ?, retry_count = ?, error_message = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?",
                status, nextRetry, errorMessage, java.util.UUID.fromString(id));

        if ("DEAD_LETTER".equals(status)) {
            log.warn("Outbox event {} moved to DEAD_LETTER after {} retries", id, MAX_RETRIES);
        }
    }

    // FIX Sonar S112: reemplazar "throws Exception" por excepciones específicas de
    // la JCA
    private String encryptPayload(String plaintext)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        if (encryptionKey == null || encryptionKey.isEmpty()) {
            throw new IllegalStateException("Encryption key not configured");
        }
        byte[] keyBytes = Base64.getDecoder().decode(encryptionKey);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = new byte[12];
        secureRandom.nextBytes(iv); // FIX Sonar S2119: usar instancia reutilizable
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));
        byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
        return Base64.getEncoder().encodeToString(combined);
    }

    // FIX Sonar S112: reemplazar "throws Exception" por excepciones específicas de
    // la JCA
    private String decryptPayload(String ciphertext)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        if (encryptionKey == null || encryptionKey.isEmpty()) {
            throw new IllegalStateException("Encryption key not configured");
        }
        byte[] combined = Base64.getDecoder().decode(ciphertext);
        byte[] iv = new byte[12];
        byte[] encrypted = new byte[combined.length - iv.length];
        System.arraycopy(combined, 0, iv, 0, iv.length);
        System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);

        byte[] keyBytes = Base64.getDecoder().decode(encryptionKey);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

}
