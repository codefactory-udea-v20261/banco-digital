package com.udea.bancodigital.shared.health;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component("identity-circuit-breaker")
@RequiredArgsConstructor
public class IdentityCircuitBreakerHealthIndicator implements HealthIndicator {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Override
    public Health health() {
        try {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("identity-service");

            if (circuitBreaker == null) {
                return Health.unknown()
                    .withDetail("reason", "Circuit breaker 'identity-service' not found")
                    .build();
            }

            CircuitBreaker.State state = circuitBreaker.getState();
            CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();

            Health.Builder builder;
            if (state == CircuitBreaker.State.CLOSED) {
                builder = Health.up()
                    .withDetail("state", "CLOSED")
                    .withDetail("status", "Service is healthy");
            } else if (state == CircuitBreaker.State.OPEN) {
                builder = Health.outOfService()
                    .withDetail("state", "OPEN")
                    .withDetail("status", "Circuit breaker is open - service is down");
            } else if (state == CircuitBreaker.State.HALF_OPEN) {
                builder = Health.down()
                    .withDetail("state", "HALF_OPEN")
                    .withDetail("status", "Circuit breaker is testing recovery");
            } else if (state == CircuitBreaker.State.METRICS_ONLY) {
                builder = Health.up()
                    .withDetail("state", "METRICS_ONLY")
                    .withDetail("status", "Circuit breaker in metrics-only mode");
            } else if (state == CircuitBreaker.State.DISABLED) {
                builder = Health.up()
                    .withDetail("state", "DISABLED")
                    .withDetail("status", "Circuit breaker is disabled");
            } else {
                builder = Health.unknown()
                    .withDetail("state", state.toString());
            }

            builder
                .withDetail("failureRate", String.format("%.2f%%", metrics.getFailureRate()))
                .withDetail("totalCalls", metrics.getNumberOfBufferedCalls())
                .withDetail("failedCalls", metrics.getNumberOfFailedCalls());

            return builder.build();

        } catch (Exception e) {
            log.error("Error checking circuit breaker health", e);
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
