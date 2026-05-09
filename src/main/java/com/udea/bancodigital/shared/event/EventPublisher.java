package com.udea.bancodigital.shared.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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
    private final AesGcmCryptoUtil cryptoUtil;

    @Value("${encryption.key}")
    private String encryptionKey;

    public boolean publishEvent(DomainEvent event) {
        if (event == null) {
            log.warn("Attempted to publish null event");
            return false;
        }

        try {
            String eventId = event.getEventId() != null ? event.getEventId() : UUID.randomUUID().toString();
            String payload = objectMapper.writeValueAsString(event);
            String encryptedPayload = cryptoUtil.encrypt(payload, encryptionKey);

            String sql = "INSERT INTO outbox_events (id, aggregate_type, aggregate_id, event_type, encrypted_payload, payload) VALUES (?, ?, ?, ?, ?, ?)";

            jdbcTemplate.update(sql,
                    UUID.fromString(eventId.replace("evt-", "").length() == 36 ? eventId.replace("evt-", "")
                            : UUID.randomUUID().toString()),
                    "Aggregate",
                    event.getAggregateId(),
                    event.getEventType(),
                    encryptedPayload,
                    payload);

            log.info("Event {} saved to outbox (encrypted)", eventId);
            return true;

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event to JSON: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Failed to serialize event for outbox", e);
        } catch (Exception e) {
            log.error("Failed to save event to outbox: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to save event to outbox", e);
        }
    }

}
