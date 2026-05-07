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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
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

    @BeforeEach
    void setUp() {
        lenient().when(circuitBreakerRegistry.circuitBreaker("kafka-publisher")).thenReturn(circuitBreaker);
        lenient().when(circuitBreaker.getState()).thenReturn(CircuitBreaker.State.CLOSED);
        lenient().when(circuitBreaker.getMetrics()).thenReturn(metrics);
    }

    @Test
    void getKafkaStatus_Healthy() {
        when(fallbackStorage.getFallbackQueueSize()).thenReturn(0L);
        when(fallbackStorage.getFallbackStats()).thenReturn("Queue Size: 0, Stats: none, Last Failure: never");

        ResponseEntity<Map<String, Object>> response = controller.getKafkaStatus();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("healthy", response.getBody().get("status"));
        assertEquals(0L, response.getBody().get("fallback_queue_size"));
    }

    @Test
    void getKafkaStatus_Degraded() {
        when(fallbackStorage.getFallbackQueueSize()).thenReturn(10L);
        when(fallbackStorage.getFallbackStats()).thenReturn("some stats");
        
        ResponseEntity<Map<String, Object>> response = controller.getKafkaStatus();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("degraded", response.getBody().get("status"));
    }

    @Test
    void getFallbackQueueInfo_Success() {
        when(fallbackStorage.getFallbackQueueSize()).thenReturn(100L);
        when(fallbackStorage.getFallbackStats()).thenReturn("some stats");
        
        ResponseEntity<Map<String, Object>> response = controller.getFallbackQueueInfo();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(100L, response.getBody().get("queue_size"));
        assertEquals(1.0, response.getBody().get("queue_utilization_percent"));
    }

    @Test
    void getCircuitBreakerMetrics_Success() {
        when(metrics.getNumberOfFailedCalls()).thenReturn(5);
        
        ResponseEntity<Map<String, Object>> response = controller.getCircuitBreakerMetrics();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("CLOSED", response.getBody().get("state"));
        assertEquals(5, response.getBody().get("failed_calls"));
    }

    @Test
    void getCircuitBreakerMetrics_NotFound() {
        when(circuitBreakerRegistry.circuitBreaker("kafka-publisher")).thenReturn(null);
        
        ResponseEntity<Map<String, Object>> response = controller.getCircuitBreakerMetrics();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("not_initialized", response.getBody().get("status"));
    }

    @Test
    void resetCircuitBreaker_Success() {
        ResponseEntity<Map<String, Object>> response = controller.resetCircuitBreaker();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        verify(circuitBreaker).reset();
    }

    @Test
    void clearFallbackQueue_Success() {
        when(fallbackStorage.getFallbackQueueSize()).thenReturn(50L);
        
        ResponseEntity<Map<String, Object>> response = controller.clearFallbackQueue();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals(50L, response.getBody().get("events_discarded"));
        verify(fallbackStorage).clearFallbackQueue();
    }

    @Test
    void getKafkaHealth_UP() {
        when(fallbackStorage.getFallbackQueueSize()).thenReturn(0L);
        
        ResponseEntity<Map<String, Object>> response = controller.getKafkaHealth();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("UP", response.getBody().get("status"));
    }

    @Test
    void getKafkaHealth_Degraded_Critical() {
        when(fallbackStorage.getFallbackQueueSize()).thenReturn(6000L);
        
        ResponseEntity<Map<String, Object>> response = controller.getKafkaHealth();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("DEGRADED", response.getBody().get("status"));
        assertTrue(((String)response.getBody().get("alert")).contains("CRITICAL"));
    }
}
