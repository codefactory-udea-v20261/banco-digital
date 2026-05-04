package com.udea.bancodigital.shared.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventFallbackStorageTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ListOperations<String, String> listOperations;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private EventFallbackStorage fallbackStorage;

    @Test
    void storeEventInFallback_ShouldStoreWhenQueueNotFull() {
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(listOperations.size(anyString())).thenReturn(5L);

        DomainEvent event = DomainEvent.builder()
                .eventId("event-123")
                .eventType("TestEvent")
                .aggregateId("agg-123")
                .build();

        boolean result = fallbackStorage.storeEventInFallback(event);

        assertThat(result).isTrue();
        verify(listOperations).rightPush(anyString(), anyString());
    }

    @Test
    void storeEventInFallback_ShouldReturnFalseWhenQueueFull() {
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.size(anyString())).thenReturn(10000L);

        DomainEvent event = DomainEvent.builder().eventId("event-123").build();

        boolean result = fallbackStorage.storeEventInFallback(event);

        assertThat(result).isFalse();
        verify(listOperations, never()).rightPush(anyString(), anyString());
    }

    @Test
    void retrieveFallbackEvents_ShouldReturnList() {
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.range(anyString(), eq(0L), anyLong())).thenReturn(List.of("event1", "event2"));

        List<String> events = fallbackStorage.retrieveFallbackEvents(2);

        assertThat(events).hasSize(2);
    }

    @Test
    void getFallbackQueueSize_ShouldReturnSize() {
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.size(anyString())).thenReturn(42L);

        long size = fallbackStorage.getFallbackQueueSize();

        assertThat(size).isEqualTo(42L);
    }

    @Test
    void clearFallbackQueue_ShouldClear() {
        fallbackStorage.clearFallbackQueue();
        verify(redisTemplate).delete(anyString());
    }

    @Test
    void getFallbackStats_ShouldReturnString() {
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(listOperations.size(anyString())).thenReturn(10L);
        when(valueOperations.get(anyString())).thenReturn("test-stat");

        String stats = fallbackStorage.getFallbackStats();

        assertThat(stats).contains("Queue Size: 10");
        assertThat(stats).contains("test-stat");
    }
}
