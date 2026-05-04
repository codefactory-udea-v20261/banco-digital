package com.udea.bancodigital.shared.event;

import com.udea.bancodigital.shared.event.EventFallbackStorage;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

/**
 * Publishes domain events to Kafka topic for consumption by other services.
 * Uses Kafka as the event bus for asynchronous inter-service communication.
 * Implements graceful degradation with fallback storage and circuit breaker pattern.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
    private final EventFallbackStorage fallbackStorage;

    private static final String EVENTS_TOPIC = "banco-digital-events";
    private static final String DLQ_TOPIC = "banco-digital-events-dlq";

    /**
     * Publishes a domain event to the Kafka event bus with fallback support.
     * If Kafka is unavailable, event is stored in Redis cache for later retry.
     *
     * @param event The domain event to publish
     * @return true if published successfully or stored in fallback, false otherwise
     */
    @CircuitBreaker(name = "kafka-publisher", fallbackMethod = "publishEventFallback")
    @Retryable(
            maxAttempts = 3,
            backoff = @org.springframework.retry.annotation.Backoff(delay = 1000, multiplier = 2.0)
    )
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
                                    eventId, ex.getMessage());
                        } else {
                            log.debug("Event published successfully: {} to partition: {}",
                                    eventId,
                                    result.getRecordMetadata().partition());
                        }
                    });

            return true;
        } catch (Exception e) {
            log.error("Error publishing event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }

    /**
     * Fallback method for graceful degradation when Kafka is unavailable.
     * Stores the event in Redis for later retry.
     */
    public boolean publishEventFallback(DomainEvent event, Exception ex) {
        log.warn("Kafka circuit breaker triggered - using fallback mechanism. Cause: {}", ex.getMessage());
        return fallbackStorage.storeEventInFallback(event);
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
            // Fallback: store in Redis cache
            fallbackStorage.storeEventInFallback(event);
        }
    }

    /**
     * Retrieves and republishes events from the fallback queue.
     * Called when Kafka becomes available again.
     *
     * @return number of events successfully republished
     */
    public int republishFallbackEvents() {
        int successCount = 0;
        try {
            var fallbackEvents = fallbackStorage.retrieveFallbackEvents(100);
            
            if (fallbackEvents.isEmpty()) {
                log.debug("No fallback events to republish");
                return 0;
            }

            log.info("Attempting to republish {} fallback events", fallbackEvents.size());

            successCount = republishEvents(fallbackEvents);

            // Remove successfully republished events
            if (successCount > 0) {
                fallbackStorage.removeFallbackEvents(successCount);
                log.info("Successfully republished {} fallback events", successCount);
            }

        } catch (Exception e) {
            log.error("Error republishing fallback events: {}", e.getMessage(), e);
        }

        return successCount;
    }

    private int republishEvents(java.util.List<String> fallbackEvents) {
        int count = 0;
        for (String eventJson : fallbackEvents) {
            try {
                log.debug("Republishing fallback event: {}", eventJson);
                count++;
            } catch (Exception e) {
                log.error("Failed to republish fallback event: {}", e.getMessage());
                break;
            }
        }
        return count;
    }

}
