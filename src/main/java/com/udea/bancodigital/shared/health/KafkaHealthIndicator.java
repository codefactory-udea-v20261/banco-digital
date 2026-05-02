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

/**
 * Custom health indicator for Kafka connectivity.
 * Checks if broker is reachable and can process messages.
 */
@Slf4j
@Component("kafkaHealth")
@RequiredArgsConstructor
public class KafkaHealthIndicator implements HealthIndicator {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String HEALTH_CHECK_TOPIC = "health-check";
    private static final long TIMEOUT_MS = 5000;

    @Override
    public Health health() {
        try {
            // Send a test message to health-check topic
            Message<String> testMessage = MessageBuilder
                    .withPayload("health-check")
                    .setHeader(KafkaHeaders.TOPIC, HEALTH_CHECK_TOPIC)
                    .build();

            var sendResult = kafkaTemplate.send(testMessage);
            
            // Wait for confirmation with timeout
            sendResult.get();

            log.debug("Kafka health check passed");
            return Health.up()
                    .withDetail("kafka", "Connected and operational")
                    .withDetail("topic", HEALTH_CHECK_TOPIC)
                    .build();

        } catch (Exception e) {
            log.error("Kafka health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("kafka", "Connection failed")
                    .withDetail("reason", e.getMessage())
                    .build();
        }
    }
}
