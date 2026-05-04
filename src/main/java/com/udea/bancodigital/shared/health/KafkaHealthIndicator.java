package com.udea.bancodigital.shared.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Custom health indicator for Kafka connectivity.
 * Checks if broker is reachable and can process messages with timeout protection.
 * Does NOT fail service health if Kafka is unavailable (degraded mode).
 */
@Slf4j
@Component("kafkaHealth")
@RequiredArgsConstructor
public class KafkaHealthIndicator implements HealthIndicator {

    private static final String KAFKA_KEY = KAFKA_KEY;


    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String HEALTH_CHECK_TOPIC = "health-check";
    private static final long TIMEOUT_MS = 5000;

    @Override
    public Health health() {
        long startTime = System.currentTimeMillis();
        try {
            // Send a test message to health-check topic
            Message<String> testMessage = MessageBuilder
                    .withPayload("health-check-" + System.currentTimeMillis())
                    .setHeader(KafkaHeaders.TOPIC, HEALTH_CHECK_TOPIC)
                    .build();

            var sendResult = kafkaTemplate.send(testMessage);
            
            // Wait for confirmation with timeout protection
            var metadata = sendResult.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("Kafka health check passed in {}ms", duration);
            
            return Health.up()
                    .withDetail(KAFKA_KEY, "Connected and operational")
                    .withDetail("topic", HEALTH_CHECK_TOPIC)
                    .withDetail("responseTime", duration + "ms")
                    .withDetail("partition", metadata.getRecordMetadata().partition())
                    .build();

        } catch (java.util.concurrent.TimeoutException e) {
            long duration = System.currentTimeMillis() - startTime;
            log.warn("Kafka health check timed out after {}ms - operating in degraded mode", duration);
            
            // Return outOfService status but don't fail - service can operate without Kafka
            return Health.outOfService()
                    .withDetail(KAFKA_KEY, "Connection timeout")
                    .withDetail("reason", "Request timed out after " + TIMEOUT_MS + "ms")
                    .withDetail("mode", "graceful-degradation")
                    .withDetail("impact", "Event publishing will be retried asynchronously")
                    .build();
                    
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.warn("Kafka health check failed after {}ms: {} - operating in degraded mode", 
                    duration, e.getMessage());
            
            // Return outOfService status instead of DOWN - service should still work
            return Health.outOfService()
                    .withDetail(KAFKA_KEY, "Connection failed")
                    .withDetail("reason", e.getMessage())
                    .withDetail("mode", "graceful-degradation")
                    .withDetail("impact", "Events will be queued for retry")
                    .build();
        }
    }
}
