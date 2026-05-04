package com.udea.bancodigital.shared.infrastructure.consumer.dlq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Dead Letter Queue Consumer for Access Provisioning.
 *
 * Monitors and alerts on failed access provisioning events that exhausted retries.
 * 
 * This consumer logs all DLQ events for investigation and alerting.
 * In production, should integrate with:
 * - Slack/PagerDuty for alerts
 * - Database for DLQ event persistence
 * - Metrics for tracking DLQ growth
 */
@Slf4j
@Component
@Profile("!prod")
@RequiredArgsConstructor
public class AccessProvisioningDLQConsumer {

    private static final String DLQ_TOPIC = "cliente-access-provisioning-dlq";
    private static final String CONSUMER_GROUP = "access-provisioning-dlq";

    /**
     * Consume access provisioning DLQ events.
     * These are events that failed after max retries.
     */
    @KafkaListener(
            topics = DLQ_TOPIC,
            groupId = CONSUMER_GROUP,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeDLQEvent(Map<String, Object> event) {
        String clienteId = String.valueOf(event.get("clienteId"));
        int retryCount = (int) event.getOrDefault("retryCount", 0);
        String reason = String.valueOf(event.get("failureReason"));

        log.error("ACCESS PROVISIONING DLQ EVENT - clienteId={}, retries={}, reason={}",
            clienteId, retryCount, reason);
    }
}
