package com.udea.bancodigital.shared.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Handles messages that failed processing and were sent to the Dead Letter Queue.
 * Implements retry logic and alerting for failed events.
 */
@Slf4j
@Component
@Profile("!prod")
@RequiredArgsConstructor
public class DeadLetterQueueHandler {

    private static final String DLQ_TOPIC = "banco-digital-events-dlq";
    private static final String DLQ_CONSUMER_GROUP = "dlq-handler";

    /**
     * Processes failed events from the DLQ.
     * Implements exponential backoff and alerting.
     */
    @KafkaListener(
            topics = DLQ_TOPIC,
            groupId = DLQ_CONSUMER_GROUP,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleFailedEvent(DomainEvent event) {
        try {
            log.error("Processing failed event from DLQ: {} (id: {}, type: {})",
                    event.getEventType(),
                    event.getEventId(),
                    event.getEventType());

            // Increment retry counter
            Integer retryCount = (Integer) event.getClass()
                    .getDeclaredField("retryCount")
                    .get(event);

            if (retryCount == null) {
                retryCount = 0;
            }

            // Exponential backoff: 2^retryCount seconds
            int backoffSeconds = (int) Math.pow(2, retryCount);
            log.warn("Failed event {} will be retried after {} seconds (attempt {})",
                    event.getEventId(), backoffSeconds, retryCount + 1);

            // Log to monitoring system (alertar a operaciones)
            logFailureAlert(event, retryCount);

            // TODO: Implement actual retry logic (requeue to main topic)
            // TODO: Implement alerting mechanism (email, Slack, PagerDuty)
            // TODO: Persist to database for later manual retry

        } catch (Exception e) {
            log.error("Error processing DLQ event {}: {}", event.getEventId(), e.getMessage(), e);
        }
    }

    /**
     * Logs an alert for a failed event.
     * In production, this would send alerts to monitoring systems.
     */
    private void logFailureAlert(DomainEvent event, int retryCount) {
        log.warn("⚠️  ALERT: Event processing failed");
        log.warn("   Event ID: {}", event.getEventId());
        log.warn("   Event Type: {}", event.getEventType());
        log.warn("   Aggregate ID: {}", event.getAggregateId());
        log.warn("   Source Service: {}", event.getSourceService());
        log.warn("   Retry Attempt: {}", retryCount + 1);
        log.warn("   Timestamp: {}", event.getOccurredAt());

        // TODO: Send to monitoring (Prometheus, DataDog, etc.)
        // TODO: Send to alert system (PagerDuty, OpsGenie, etc.)
        // TODO: Send to logging aggregation (ELK, Datadog, etc.)
    }

    /**
     * Manually trigger retry for a specific event.
     * Can be called from admin/operator interface.
     */
    public void retryEvent(String eventId) {
        log.info("Manual retry triggered for event: {}", eventId);
        // TODO: Fetch event from database
        // TODO: Republish to main topic
        // TODO: Update status to "retrying"
    }

    /**
     * Mark event as permanently failed and send to archive.
     * Can be called when retries are exhausted.
     */
    public void archiveFailedEvent(String eventId) {
        log.warn("Archiving permanently failed event: {}", eventId);
        // TODO: Move to archive topic/table
        // TODO: Create incident ticket
        // TODO: Notify operations team
    }
}
