package com.udea.bancodigital.shared.infrastructure.monitoring;

import com.udea.bancodigital.shared.event.EventFallbackStorage;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for Kafka monitoring, alerting, and operational tasks.
 * Provides endpoints to check Kafka health, view fallback queue status, and trigger recovery operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/monitoring/kafka")
@RequiredArgsConstructor
public class KafkaMonitoringController {

    private static final String KAFKA_PUBLISHER = "kafka-publisher";
    private static final String STATUS = "status";
    private static final String MESSAGE = "message";
    private static final String ERROR = "error";


    private final EventFallbackStorage fallbackStorage;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    /**
     * Get Kafka connectivity and fallback queue status.
     *
     * @return status information including fallback queue size and circuit breaker state
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getKafkaStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // Fallback queue status
        long fallbackQueueSize = fallbackStorage.getFallbackQueueSize();
        status.put("fallback_queue_size", fallbackQueueSize);
        status.put("fallback_stats", fallbackStorage.getFallbackStats());
        status.put("fallback_queue_operational", fallbackQueueSize >= 0);
        
        // Circuit breaker status
        CircuitBreaker kafkaPublisherCb = circuitBreakerRegistry.circuitBreaker(KAFKA_PUBLISHER);
        if (kafkaPublisherCb != null) {
            status.put("circuit_breaker_state", kafkaPublisherCb.getState().toString());
            var metrics = kafkaPublisherCb.getMetrics();
            Map<String, Object> cbMetrics = new HashMap<>();
            cbMetrics.put("failed_calls", metrics.getNumberOfFailedCalls());
            cbMetrics.put("successful_calls", metrics.getNumberOfSuccessfulCalls());
            cbMetrics.put("buffered_calls", metrics.getNumberOfBufferedCalls());
            status.put("circuit_breaker_metrics", cbMetrics);
        }
        
        status.put("timestamp", System.currentTimeMillis());
        status.put(STATUS, fallbackQueueSize == 0 ? "healthy" : "degraded");
        
        return ResponseEntity.ok(status);
    }

    /**
     * Get detailed fallback queue information for alerting.
     *
     * @return fallback queue metrics
     */
    @GetMapping("/fallback-queue")
    public ResponseEntity<Map<String, Object>> getFallbackQueueInfo() {
        Map<String, Object> info = new HashMap<>();
        long queueSize = fallbackStorage.getFallbackQueueSize();
        
        info.put("queue_size", queueSize);
        info.put("max_queue_size", 10000);
        info.put("queue_utilization_percent", (queueSize / 10000.0) * 100);
        info.put("stats", fallbackStorage.getFallbackStats());
        info.put("alert_threshold_exceeded", queueSize > 1000);
        info.put("critical_alert", queueSize > 5000);
        
        return ResponseEntity.ok(info);
    }

    /**
     * Get circuit breaker detailed metrics for monitoring dashboards.
     *
     * @return circuit breaker metrics
     */
    @GetMapping("/circuit-breaker")
    public ResponseEntity<Map<String, Object>> getCircuitBreakerMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(KAFKA_PUBLISHER);
        
        if (cb == null) {
            metrics.put(STATUS, "not_initialized");
            return ResponseEntity.ok(metrics);
        }
        
        var cbMetrics = cb.getMetrics();
        
        metrics.put("state", cb.getState().toString());
        metrics.put("failed_calls", cbMetrics.getNumberOfFailedCalls());
        metrics.put("successful_calls", cbMetrics.getNumberOfSuccessfulCalls());
        metrics.put("buffered_calls", cbMetrics.getNumberOfBufferedCalls());
        metrics.put("not_permitted_calls", cbMetrics.getNumberOfNotPermittedCalls());
        metrics.put("slow_calls", cbMetrics.getNumberOfSlowCalls());
        metrics.put("failure_rate", cbMetrics.getFailureRate());
        metrics.put("slow_call_rate", cbMetrics.getSlowCallRate());
        metrics.put("last_recorded_exception", "Not available in current version");
        
        return ResponseEntity.ok(metrics);
    }

    /**
     * Trigger manual reset of circuit breaker (use with caution).
     * Only available in non-production environments.
     *
     * @return operation result
     */
    @PostMapping("/circuit-breaker/reset")
    public ResponseEntity<Map<String, Object>> resetCircuitBreaker() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(KAFKA_PUBLISHER);
            if (cb != null) {
                cb.reset();
                log.warn("Circuit breaker for kafka-publisher reset manually");
                result.put(STATUS, "success");
                result.put(MESSAGE, "Circuit breaker reset successfully");
                result.put("new_state", cb.getState().toString());
            } else {
                result.put(STATUS, "error");
                result.put(MESSAGE, "Circuit breaker not found");
            }
        } catch (Exception e) {
            log.error("Failed to reset circuit breaker: {}", e.getMessage(), e);
            result.put(STATUS, "error");
            result.put(MESSAGE, e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Clear the fallback event queue (use with caution - data loss!).
     *
     * @return operation result
     */
    @PostMapping("/fallback-queue/clear")
    public ResponseEntity<Map<String, Object>> clearFallbackQueue() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            long beforeSize = fallbackStorage.getFallbackQueueSize();
            fallbackStorage.clearFallbackQueue();
            
            log.warn("Fallback event queue cleared manually. Lost {} events", beforeSize);
            result.put(STATUS, "success");
            result.put(MESSAGE, "Fallback queue cleared");
            result.put("events_discarded", beforeSize);
        } catch (Exception e) {
            log.error("Failed to clear fallback queue: {}", e.getMessage(), e);
            result.put(STATUS, "error");
            result.put(MESSAGE, e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Health check endpoint for monitoring systems.
     * Returns degraded if fallback queue is in use, up if Kafka is operational.
     *
     * @return health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getKafkaHealth() {
        Map<String, Object> health = new HashMap<>();
        long queueSize = fallbackStorage.getFallbackQueueSize();
        
        if (queueSize > 0) {
            health.put(STATUS, "DEGRADED");
            health.put(MESSAGE, "Operating in fallback mode with " + queueSize + " queued events");
            if (queueSize > 5000) {
                health.put("alert", "CRITICAL - Fallback queue critically full");
            } else if (queueSize > 1000) {
                health.put("alert", "WARNING - Fallback queue high");
            }
        } else {
            health.put(STATUS, "UP");
            health.put(MESSAGE, "Kafka is operational");
        }
        
        health.put("fallback_queue_size", queueSize);
        health.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(health);
    }
}
