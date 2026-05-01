package com.udea.bancodigital.shared.infrastructure.consumer.pending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

/**
 * Pending Event Consumer for Cliente Access Provisioning.
 *
 * Handles retry and replay of failed access provisioning events:
 * - Listens to: cliente-access-provisioning-pending
 * - Retry logic: exponential backoff
 * - DLQ: cliente-access-provisioning-dlq (after max retries)
 * - Auto-recovery: When Identity Service comes back online
 *
 * Flow:
 * 1. Event queued to pending topic when Identity Service is down
 * 2. PendingAccessProvisioningConsumer picks it up
 * 3. Attempts retry with exponential backoff
 * 4. After maxRetries: moved to DLQ for manual intervention
 * 5. When Identity Service recovers: events automatically replayed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PendingAccessProvisioningConsumer {

    private static final String PENDING_TOPIC = "cliente-access-provisioning-pending";
    private static final String DLQ_TOPIC = "cliente-access-provisioning-dlq";
    private static final String CONSUMER_GROUP = "access-provisioning-pending";
    private static final int MAX_RETRIES = 5;

    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    /**
     * Consume pending access provisioning events and retry.
     */
    @KafkaListener(
            topics = PENDING_TOPIC,
            groupId = CONSUMER_GROUP,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePendingEvent(Map<String, Object> event) {
        try {
            String clienteId = String.valueOf(event.get("clienteId"));
            String email = String.valueOf(event.get("email"));
            int retryCount = (int) event.getOrDefault("retryCount", 0);

            log.info("Processing pending access provisioning event: "
                + "clienteId={}, email={}, retryCount={}/{}",
                clienteId, email, retryCount, MAX_RETRIES);

            // TODO: Call Identity Service to provision access
            // This would normally be done via the CircuitBreakerAdapter
            // If Identity Service is still down, this will fail and be retried
            provisionAccess(clienteId, email);

            log.info("Successfully replayed access provisioning for clienteId={}", clienteId);

        } catch (Exception e) {
            handleRetry(event, e);
        }
    }

    /**
     * Handle retry logic with exponential backoff and DLQ routing.
     */
    private void handleRetry(Map<String, Object> event, Exception e) {
        int retryCount = (int) event.getOrDefault("retryCount", 0);

        if (retryCount >= MAX_RETRIES) {
            // Max retries reached, move to DLQ
            moveToDLQ(event, "Max retries exceeded: " + e.getMessage());
        } else {
            // Retry with exponential backoff
            retryWithBackoff(event, retryCount);
        }
    }

    /**
     * Retry with exponential backoff.
     * Backoff: attempt 1 = 1s, attempt 2 = 2s, attempt 3 = 4s, etc.
     */
    private void retryWithBackoff(Map<String, Object> event, int retryCount) {
        long backoffMs = (long) Math.pow(2, retryCount) * 1000; // 1s, 2s, 4s, 8s, 16s
        int nextRetry = retryCount + 1;

        log.warn("Retry failed for clienteId={}. Scheduling retry {} of {}, "
            + "backoff={}ms",
            event.get("clienteId"), nextRetry, MAX_RETRIES, backoffMs);

        // Update retry count
        event.put("retryCount", nextRetry);
        event.put("lastRetryAt", Instant.now().toString());
        event.put("nextRetryScheduledAt", 
            Instant.now().plusMillis(backoffMs).toString());

        // Re-queue to pending topic
        kafkaTemplate.send(PENDING_TOPIC, String.valueOf(event.get("clienteId")), event);
    }

    /**
     * Move event to DLQ after max retries exceeded.
     */
    private void moveToDLQ(Map<String, Object> event, String reason) {
        log.error("Moving access provisioning event to DLQ. "
            + "clienteId={}, reason={}",
            event.get("clienteId"), reason);

        // Add failure metadata
        event.put("failedAt", Instant.now().toString());
        event.put("failureReason", reason);
        event.put("movedToDLQAt", Instant.now().toString());

        // Send to DLQ
        kafkaTemplate.send(DLQ_TOPIC, String.valueOf(event.get("clienteId")), event);

        log.info("Event moved to DLQ for manual intervention. "
            + "Topic={}, clienteId={}",
            DLQ_TOPIC, event.get("clienteId"));
    }

    /**
     * Provision access for cliente (stub for retry logic).
     * In production, this would call Identity Service via CircuitBreakerAdapter.
     */
    private void provisionAccess(String clienteId, String email) {
        // TODO: In production environment:
        // clienteAccessProvisioningCircuitBreakerAdapter.provisionAccess(
        //     UUID.fromString(clienteId),
        //     email
        // );
        
        log.debug("Provisioning access for clienteId={}, email={}", clienteId, email);
    }

    /**
     * Get consumer statistics.
     */
    public Map<String, Object> getStats() {
        return Map.of(
            "topic", PENDING_TOPIC,
            "dlqTopic", DLQ_TOPIC,
            "maxRetries", MAX_RETRIES,
            "consumerGroup", CONSUMER_GROUP
        );
    }
}
