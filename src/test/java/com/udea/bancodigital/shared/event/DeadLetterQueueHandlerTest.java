package com.udea.bancodigital.shared.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith(MockitoExtension.class)
class DeadLetterQueueHandlerTest {

    @InjectMocks
    private DeadLetterQueueHandler handler;

    @Test
    void handleFailedEvent_ShouldExecuteWithoutException() {
        DomainEvent event = DomainEvent.builder()
                .eventId("123")
                .eventType("TestEvent")
                .aggregateId("agg1")
                .sourceService("test-service")
                .build();

        assertThatCode(() -> handler.handleFailedEvent(event))
                .doesNotThrowAnyException();
    }

    @Test
    void retryEvent_ShouldLogAndNotThrow() {
        assertThatCode(() -> handler.retryEvent("event-123"))
                .doesNotThrowAnyException();
    }

    @Test
    void archiveFailedEvent_ShouldLogAndNotThrow() {
        assertThatCode(() -> handler.archiveFailedEvent("event-123"))
                .doesNotThrowAnyException();
    }
}
