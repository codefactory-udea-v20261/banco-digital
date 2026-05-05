package com.udea.bancodigital.shared.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Value;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

/**
 * Publishes domain events using the Transactional Outbox Pattern.
 * Saves events to the database within the current transaction context.
 * An external processor will later relay these to Kafka.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final EventFallbackStorage fallbackStorage; // Kept for backwards compatibility

    @Value("${encryption.key}")
    private String encryptionKey;

    public boolean publishEvent(DomainEvent event) {
        try {
            if (event == null) {
                log.warn("Attempted to publish null event");
                return false;
            }

            String eventId = event.getEventId() != null ? event.getEventId() : UUID.randomUUID().toString();
            String payload = objectMapper.writeValueAsString(event);
            String encryptedPayload = encryptPayload(payload);

            // Writing to both encrypted_payload and payload (deprecated) for backward compatibility
            String sql = "INSERT INTO outbox_events (id, aggregate_type, aggregate_id, event_type, encrypted_payload, payload) VALUES (?, ?, ?, ?, ?, ?)";
            
            jdbcTemplate.update(sql,
                    UUID.fromString(eventId.replace("evt-", "").length() == 36 ? eventId.replace("evt-", "") : UUID.randomUUID().toString()),
                    "Aggregate", // Or extract from event if available
                    event.getAggregateId(),
                    event.getEventType(),
                    encryptedPayload,
                    payload);

            log.info("Event {} saved to outbox (encrypted)", eventId);
            return true;
        } catch (Exception e) {
            log.error("Failed to save event to outbox: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save event to outbox", e);
        }
    }

    private String encryptPayload(String plaintext) throws Exception {
        if (encryptionKey == null || encryptionKey.isEmpty()) {
            throw new IllegalStateException("Encryption key not configured");
        }
        byte[] keyBytes = Base64.getDecoder().decode(encryptionKey);
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
