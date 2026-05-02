package com.udea.bancodigital.shared.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class for all domain events in the Banco Digital system.
 * Events are immutable facts about something that has occurred.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DomainEvent implements Serializable {

    /**
     * Unique identifier for this specific event instance
     */
    @JsonProperty("event_id")
    private String eventId;

    /**
     * Type of event (e.g., "CustomerCreated", "TransactionCompleted")
     */
    @JsonProperty("event_type")
    private String eventType;

    /**
     * Aggregate ID (e.g., customerId, transactionId) that this event relates to
     */
    @JsonProperty("aggregate_id")
    private String aggregateId;

    /**
     * Root aggregate ID for correlation across sagas
     */
    @JsonProperty("correlation_id")
    private String correlationId;

    /**
     * Saga instance ID for tracking multi-step processes
     */
    @JsonProperty("saga_id")
    private String sagaId;

    /**
     * Timestamp when the event occurred (in the service)
     */
    @JsonProperty("occurred_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime occurredAt;

    /**
     * Service that originated this event
     */
    @JsonProperty("source_service")
    private String sourceService;

    /**
     * Version of the event schema
     */
    @JsonProperty("version")
    private Integer version;

    /**
     * User ID who triggered the event
     */
    @JsonProperty("user_id")
    private String userId;

    public static DomainEvent createEvent(
            String eventType,
            String aggregateId,
            String sourceService) {
        return DomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .aggregateId(aggregateId)
                .correlationId(aggregateId)
                .sourceService(sourceService)
                .occurredAt(LocalDateTime.now())
                .version(1)
                .build();
    }

    public static DomainEvent createEventWithSaga(
            String eventType,
            String aggregateId,
            String sagaId,
            String sourceService) {
        DomainEvent event = createEvent(eventType, aggregateId, sourceService);
        event.setSagaId(sagaId);
        return event;
    }
}
