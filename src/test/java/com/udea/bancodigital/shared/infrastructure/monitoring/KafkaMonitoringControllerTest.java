package com.udea.bancodigital.shared.infrastructure.monitoring;

import com.udea.bancodigital.shared.event.EventFallbackStorage;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaMonitoringControllerTest {

    @Mock
    private EventFallbackStorage fallbackStorage;

    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Mock
    private CircuitBreaker circuitBreaker;

    @Mock
    private CircuitBreaker.Metrics metrics;

    @InjectMocks
    private KafkaMonitoringController controller;

    @Test
    void getKafkaStatus_ShouldReturnHealthyWhenQueueEmpty() {
        when(fallbackStorage.getFallbackQueueSize()).thenReturn(0L);
        when(circuitBreakerRegistry.circuitBreaker("kafka-publisher")).thenReturn(circuitBreaker);
        when(circuitBreaker.getState()).thenReturn(CircuitBreaker.State.CLOSED);
        when(circuitBreaker.getMetrics()).thenReturn(metrics);

        ResponseEntity<Map<String, Object>> response = controller.getKafkaStatus();
        
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().get("status")).isEqualTo("healthy");
    }

    @Test
    void getFallbackQueueInfo_ShouldReturnMetrics() {
        when(fallbackStorage.getFallbackQueueSize()).thenReturn(1500L);
        when(fallbackStorage.getFallbackStats()).thenReturn("Stats");

        ResponseEntity<Map<String, Object>> response = controller.getFallbackQueueInfo();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().get("queue_size")).isEqualTo(1500L);
        assertThat(response.getBody().get("alert_threshold_exceeded")).isEqualTo(true);
    }

    @Test
    void resetCircuitBreaker_ShouldResetAndReturnSuccess() {
        when(circuitBreakerRegistry.circuitBreaker("kafka-publisher")).thenReturn(circuitBreaker);
        when(circuitBreaker.getState()).thenReturn(CircuitBreaker.State.CLOSED);

        ResponseEntity<Map<String, Object>> response = controller.resetCircuitBreaker();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().get("status")).isEqualTo("success");
        verify(circuitBreaker).reset();
    }

    @Test
    void clearFallbackQueue_ShouldClearAndReturnSuccess() {
        when(fallbackStorage.getFallbackQueueSize()).thenReturn(5L);

        ResponseEntity<Map<String, Object>> response = controller.clearFallbackQueue();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().get("status")).isEqualTo("success");
        verify(fallbackStorage).clearFallbackQueue();
    }

    @Test
    void getKafkaHealth_ShouldReturnDegradedWhenQueueNotEmpty() {
        when(fallbackStorage.getFallbackQueueSize()).thenReturn(10L);

        ResponseEntity<Map<String, Object>> response = controller.getKafkaHealth();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().get("status")).isEqualTo("DEGRADED");
    }
}
