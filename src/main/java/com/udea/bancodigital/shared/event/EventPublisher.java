package com.udea.bancodigital.shared.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * Publishes domain events to Kafka topic for consumption by other services.
 * Uses Kafka as the event bus for asynchronous inter-service communication.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    private static final String EVENTS_TOPIC = "banco-digital-events";
    private static final String DLQ_TOPIC = "banco-digital-events-dlq";

    /**
     * Publishes a domain event to the Kafka event bus.
     *
     * @param event The domain event to publish
     * @return true if published successfully, false otherwise
     */
    public boolean publishEvent(DomainEvent event) {
        try {
            if (event == null) {
                log.warn("Attempted to publish null event");
                return false;
            }

            String eventId = event.getEventId();
            String eventType = event.getEventType();

            log.info("Publishing event: {} (id: {}, aggregateId: {})",
                    eventType, eventId, event.getAggregateId());

            Message<DomainEvent> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, EVENTS_TOPIC)
                    .setHeader(KafkaHeaders.KEY, event.getAggregateId())
                    .setHeader("event_type", eventType)
                    .setHeader("event_id", eventId)
                    .setHeader("correlation_id", event.getCorrelationId())
                    .setHeader("source_service", event.getSourceService())
                    .build();

            kafkaTemplate.send(message)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish event {}: {}",
                                    eventId, ex.getMessage(), ex);
                        } else {
                            log.debug("Event published successfully: {} to partition: {}",
                                    eventId,
                                    result.getRecordMetadata().partition());
                        }
                    });

            return true;
        } catch (Exception e) {
            log.error("Error publishing event: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Publishes an event directly to the Dead Letter Queue (for processing failures).
     *
     * @param event The event that failed processing
     * @param reason The reason it was sent to DLQ
     */
    public void publishEventToDLQ(DomainEvent event, String reason) {
        try {
            log.warn("Sending event {} to DLQ. Reason: {}", event.getEventId(), reason);

            Message<DomainEvent> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, DLQ_TOPIC)
                    .setHeader(KafkaHeaders.KEY, event.getAggregateId())
                    .setHeader("dlq_reason", reason)
                    .setHeader("original_event_type", event.getEventType())
                    .build();

            kafkaTemplate.send(message);
        } catch (Exception e) {
            log.error("Failed to send event {} to DLQ: {}", event.getEventId(), e.getMessage(), e);
        }
    }
}
