package com.udea.bancodigital.shared.health;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdentityCircuitBreakerHealthIndicatorTest {

    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Mock
    private CircuitBreaker circuitBreaker;

    @Mock
    private CircuitBreaker.Metrics metrics;

    @InjectMocks
    private IdentityCircuitBreakerHealthIndicator healthIndicator;

    @Test
    void health_ShouldReturnUpWhenClosed() {
        when(circuitBreakerRegistry.circuitBreaker("identity-service")).thenReturn(circuitBreaker);
        when(circuitBreaker.getState()).thenReturn(CircuitBreaker.State.CLOSED);
        when(circuitBreaker.getMetrics()).thenReturn(metrics);

        Health health = healthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    void health_ShouldReturnOutOfServiceWhenOpen() {
        when(circuitBreakerRegistry.circuitBreaker("identity-service")).thenReturn(circuitBreaker);
        when(circuitBreaker.getState()).thenReturn(CircuitBreaker.State.OPEN);
        when(circuitBreaker.getMetrics()).thenReturn(metrics);

        Health health = healthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.OUT_OF_SERVICE);
    }

    @Test
    void health_ShouldReturnDownWhenHalfOpen() {
        when(circuitBreakerRegistry.circuitBreaker("identity-service")).thenReturn(circuitBreaker);
        when(circuitBreaker.getState()).thenReturn(CircuitBreaker.State.HALF_OPEN);
        when(circuitBreaker.getMetrics()).thenReturn(metrics);

        Health health = healthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
    }

    @Test
    void health_ShouldReturnUnknownWhenNotFound() {
        when(circuitBreakerRegistry.circuitBreaker("identity-service")).thenReturn(null);

        Health health = healthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UNKNOWN);
    }

    @Test
    void health_ShouldReturnDownOnException() {
        when(circuitBreakerRegistry.circuitBreaker("identity-service")).thenThrow(new RuntimeException("Error"));

        Health health = healthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
    }

    @Test
    void health_ShouldReturnUpWhenMetricsOnly() {
        when(circuitBreakerRegistry.circuitBreaker("identity-service")).thenReturn(circuitBreaker);
        when(circuitBreaker.getState()).thenReturn(CircuitBreaker.State.METRICS_ONLY);
        when(circuitBreaker.getMetrics()).thenReturn(metrics);

        Health health = healthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    void health_ShouldReturnUpWhenDisabled() {
        when(circuitBreakerRegistry.circuitBreaker("identity-service")).thenReturn(circuitBreaker);
        when(circuitBreaker.getState()).thenReturn(CircuitBreaker.State.DISABLED);
        when(circuitBreaker.getMetrics()).thenReturn(metrics);

        Health health = healthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }
}
