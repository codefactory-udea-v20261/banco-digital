package com.udea.bancodigital.shared.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class DeadLetterQueueHandlerTest {

    @InjectMocks
    private DeadLetterQueueHandler dlqHandler;

    @Test
    void handleFailedEvent_Success() {
        DomainEvent event = new DomainEvent() {
            @Override public String getEventId() { return "evt-123"; }
            @Override public String getEventType() { return "TEST_TYPE"; }
            @Override public String getAggregateId() { return "agg-1"; }
            @Override public String getSourceService() { return "test-service"; }
            @Override public LocalDateTime getOccurredAt() { return LocalDateTime.now(); }
        };

        // Note: retryCount is accessed via reflection in the handler
        assertDoesNotThrow(() -> dlqHandler.handleFailedEvent(event));
    }

    @Test
    void handleFailedEvent_WithException_DoesNotPropagate() {
        // Passing an event that will cause an exception during field access (reflection)
        DomainEvent event = new DomainEvent() {
            @Override public String getEventId() { return "evt-123"; }
        };
        // This will trigger NoSuchFieldException inside the handler because it's an anonymous class without 'retryCount'
        assertDoesNotThrow(() -> dlqHandler.handleFailedEvent(event));
    }

    @Test
    void retryEvent_DoesNotThrow() {
        assertDoesNotThrow(() -> dlqHandler.retryEvent("evt-123"));
    }

    @Test
    void archiveFailedEvent_DoesNotThrow() {
        assertDoesNotThrow(() -> dlqHandler.archiveFailedEvent("evt-123"));
    }
}
