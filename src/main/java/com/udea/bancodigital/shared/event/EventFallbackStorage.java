package com.udea.bancodigital.shared.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

/**
 * Fallback event storage using Redis cache.
 * Stores events when Kafka is unavailable, enabling graceful degradation.
 * Events are queued for retry when Kafka reconnects.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventFallbackStorage {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String FALLBACK_QUEUE_KEY = "kafka:fallback:events";
    private static final String FALLBACK_STATS_KEY = "kafka:fallback:stats";
    private static final String LAST_KAFKA_FAILURE_KEY = "kafka:last-failure";
    private static final int MAX_FALLBACK_EVENTS = 10000;

    /**
     * Stores an event in the fallback queue when Kafka is unavailable.
     *
     * @param event the event to store
     * @return true if stored successfully, false if queue is full
     */
    public boolean storeEventInFallback(DomainEvent event) {
        try {
            // Check queue size
            Long queueSize = redisTemplate.opsForList().size(FALLBACK_QUEUE_KEY);
            if (queueSize != null && queueSize >= MAX_FALLBACK_EVENTS) {
                log.error("Fallback event queue is full ({} events). Event {} will be lost!",
                        queueSize, event.getEventId());
                return false;
            }

            // Serialize and store event
            String eventJson = serializeEvent(event);
            redisTemplate.opsForList().rightPush(FALLBACK_QUEUE_KEY, eventJson);
            
            // Update statistics
            updateFallbackStats();
            recordKafkaFailure();
            
            log.warn("Event {} stored in fallback queue (size: {})",
                    event.getEventId(), queueSize != null ? queueSize + 1 : 1);
            
            return true;
            
        } catch (Exception e) {
            log.error("Failed to store event in fallback queue: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Retrieves all events from the fallback queue.
     *
     * @return list of serialized events
     */
    public List<String> retrieveFallbackEvents(int limit) {
        try {
            return redisTemplate.opsForList().range(FALLBACK_QUEUE_KEY, 0, limit - 1);
        } catch (Exception e) {
            log.error("Failed to retrieve fallback events: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Removes events from the fallback queue after successful republishing.
     *
     * @param count number of events to remove
     */
    public void removeFallbackEvents(int count) {
        try {
            for (int i = 0; i < count; i++) {
                redisTemplate.opsForList().leftPop(FALLBACK_QUEUE_KEY);
            }
            log.info("Removed {} events from fallback queue", count);
        } catch (Exception e) {
            log.error("Failed to remove fallback events: {}", e.getMessage());
        }
    }

    /**
     * Gets the current size of the fallback queue.
     *
     * @return queue size
     */
    public long getFallbackQueueSize() {
        try {
            Long size = redisTemplate.opsForList().size(FALLBACK_QUEUE_KEY);
            return size != null ? size : 0;
        } catch (Exception e) {
            log.error("Failed to get fallback queue size: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Clears the fallback queue (use with caution).
     */
    public void clearFallbackQueue() {
        try {
            redisTemplate.delete(FALLBACK_QUEUE_KEY);
            log.warn("Fallback event queue cleared");
        } catch (Exception e) {
            log.error("Failed to clear fallback queue: {}", e.getMessage());
        }
    }

    /**
     * Gets statistics about fallback queue usage.
     *
     * @return stats as a comma-separated string
     */
    public String getFallbackStats() {
        try {
            Long size = redisTemplate.opsForList().size(FALLBACK_QUEUE_KEY);
            String stats = (String) redisTemplate.opsForValue().get(FALLBACK_STATS_KEY);
            String lastFailure = (String) redisTemplate.opsForValue().get(LAST_KAFKA_FAILURE_KEY);
            
            return String.format("Queue Size: %d, Stats: %s, Last Failure: %s",
                    size != null ? size : 0,
                    stats != null ? stats : "none",
                    lastFailure != null ? lastFailure : "never");
        } catch (Exception e) {
            log.error("Failed to get fallback stats: {}", e.getMessage());
            return "stats unavailable";
        }
    }

    private String serializeEvent(DomainEvent event) {
        // Simple JSON serialization (in production, use proper JSON mapper)
        return String.format(
                "{\"id\":\"%s\",\"type\":\"%s\",\"aggregateId\":\"%s\",\"timestamp\":%d}",
                event.getEventId(),
                event.getEventType(),
                event.getAggregateId(),
                System.currentTimeMillis()
        );
    }

    private void updateFallbackStats() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_TIME);
            redisTemplate.opsForValue().set(FALLBACK_STATS_KEY, "Updated: " + timestamp);
        } catch (Exception e) {
            log.debug("Failed to update stats: {}", e.getMessage());
        }
    }

    private void recordKafkaFailure() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            redisTemplate.opsForValue().set(LAST_KAFKA_FAILURE_KEY, timestamp);
        } catch (Exception e) {
            log.debug("Failed to record Kafka failure: {}", e.getMessage());
        }
    }
}
