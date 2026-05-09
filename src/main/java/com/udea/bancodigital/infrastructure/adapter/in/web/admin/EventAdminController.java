package com.udea.bancodigital.infrastructure.adapter.in.web.admin;

import com.udea.bancodigital.shared.event.DomainEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Admin endpoint for event management and data migration.
 * Allows replaying historical events to Reporting service for initialization.
 * 
 * ⚠️ RESTRICTED TO ADMIN USERS ONLY - ENDPOINTS DISABLED IN PRODUCTION
 * 
 * Security Notes:
 * - All endpoints require ADMIN role
 * - /publish-test and /health have rate limiting to prevent Kafka flooding
 * - /replay endpoint is unimplemented (requires event store integration)
 */
@RestController
@RequestMapping("/api/v1/admin/events")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class EventAdminController {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
    private static final String EVENTS_TOPIC = "domain-events";

    // Rate limiting: max 5 requests per minute per endpoint
    private static final int RATE_LIMIT_REQUESTS = 5;
    private static final long RATE_LIMIT_WINDOW_MS = 60000;

    private final ConcurrentHashMap<String, RateLimitEntry> rateLimitMap = new ConcurrentHashMap<>();

    /**
     * Replay historical events to Kafka for data migration.
     *
     * ⚠️ NOT YET IMPLEMENTED - Returns 501 Not Implemented
     * Requires integration with OutboxEventRepository
     *
     * @param from start of the replay time range
     * @param to   end of the replay time range
     * @return 501 Not Implemented
     * @deprecated This endpoint is not yet implemented and will be removed.
     *             Use OutboxEventService for event replay instead.
     */
    @PostMapping("/replay")
    @Deprecated(forRemoval = true)
    public ResponseEntity<ReplayResponse> replayEvents(
            @RequestParam String from,
            @RequestParam String to) {

        log.warn("⚠️  Replay endpoint is not yet implemented. Please use OutboxEventService for manual migration.");

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(ReplayResponse.builder()
                        .success(false)
                        .message("Endpoint not yet implemented. Use OutboxEventService for event replay.")
                        .replayedCount(0)
                        .build());
    }

    /**
     * Publish a test event for debugging purposes.
     *
     * ⚠️ RATE LIMITED - Max 5 events/minute
     * This endpoint should only be used in development/testing
     */
    @PostMapping("/publish-test")
    public ResponseEntity<String> publishTestEvent() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!checkRateLimit("publish-test", userId)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Rate limit exceeded. Max 5 requests per minute.");
        }

        log.info("📤 Publishing test event...");

        DomainEvent testEvent = DomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("TestEvent")
                .aggregateId("test-123")
                .correlationId("test-123")
                .sourceService("admin-api")
                .occurredAt(LocalDateTime.now())
                .version(1)
                .build();

        kafkaTemplate.send(EVENTS_TOPIC, testEvent.getEventId(), testEvent);

        return ResponseEntity.ok("Test event published: " + testEvent.getEventId());
    }

    /**
     * Check Kafka connectivity and topic availability.
     *
     * ⚠️ RATE LIMITED - Max 5 health checks/minute
     */
    @GetMapping("/health")
    public ResponseEntity<String> checkHealth() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!checkRateLimit("health", userId)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Rate limit exceeded. Max 5 health checks per minute.");
        }

        try {
            DomainEvent healthCheck = DomainEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("HealthCheck")
                    .aggregateId("health-check")
                    .sourceService("admin-api")
                    .occurredAt(LocalDateTime.now())
                    .version(1)
                    .build();

            var future = kafkaTemplate.send(EVENTS_TOPIC, healthCheck.getEventId(), healthCheck);
            future.get();

            return ResponseEntity.ok("✅ Kafka is healthy");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // FIX Sonar S2142: re-interrupt the thread
            log.error("❌ Kafka health check interrupted", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("❌ Kafka health check interrupted: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Kafka health check failed", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("❌ Kafka is unavailable: " + e.getMessage());
        }
    }

    /**
     * Rate limiting implementation.
     */
    private boolean checkRateLimit(String endpoint, String userId) {
        String key = endpoint + ":" + userId;
        long now = System.currentTimeMillis();

        // Fix memory leak: evict expired entries
        rateLimitMap.entrySet().removeIf(e -> now - e.getValue().windowStartTime > RATE_LIMIT_WINDOW_MS);

        RateLimitEntry entry = rateLimitMap.compute(key, (k, existing) -> {
            if (existing == null || (now - existing.windowStartTime) > RATE_LIMIT_WINDOW_MS) {
                return new RateLimitEntry(now, new AtomicInteger(1));
            } else {
                existing.requestCount.incrementAndGet();
                return existing;
            }
        });

        return entry.requestCount.get() <= RATE_LIMIT_REQUESTS;
    }

    @Data
    @Builder
    public static class ReplayResponse {
        private boolean success;
        private String message;
        private int replayedCount;
        private LocalDateTime fromTimestamp;
        private LocalDateTime toTimestamp;
    }

    @Data
    static class RateLimitEntry {
        long windowStartTime;
        AtomicInteger requestCount;

        RateLimitEntry(long windowStartTime, AtomicInteger requestCount) {
            this.windowStartTime = windowStartTime;
            this.requestCount = requestCount;
        }
    }
}
