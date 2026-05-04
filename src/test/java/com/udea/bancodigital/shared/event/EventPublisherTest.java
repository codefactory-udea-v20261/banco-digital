package com.udea.bancodigital.shared.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for EventPublisher component.
 * Validates event publishing to Kafka.
 */
@DisplayName("EventPublisher Tests")
class EventPublisherTest {

    private EventPublisher eventPublisher;
    private KafkaTemplate<String, DomainEvent> kafkaTemplate;
    private EventFallbackStorage fallbackStorage;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        fallbackStorage = mock(EventFallbackStorage.class);
        eventPublisher = new EventPublisher(kafkaTemplate, fallbackStorage);
    }

    @Test
    @DisplayName("Should publish CustomerCreatedEvent successfully")
    void testPublishCustomerCreatedEvent() {
        String customerId = UUID.randomUUID().toString();
        String email = "customer@example.com";
        String fullName = "John Doe";
        String documentType = "CC";
        String documentNumber = "12345678";
        String phone = "+573001234567";
        String userId = "user-123";

        CustomerCreatedEvent event = CustomerCreatedEvent.from(
                customerId, email, fullName, documentType, documentNumber, phone, userId
        );

        CompletableFuture<SendResult<String, DomainEvent>> future = CompletableFuture.completedFuture(mock(SendResult.class));
        when(kafkaTemplate.send(any(Message.class))).thenReturn(future);

        boolean result = eventPublisher.publishEvent(event);

        assertTrue(result, "Event should be published successfully");
        assertNotNull(event.getEventId(), "Event ID should not be null");
        assertEquals("CustomerCreated", event.getEventType());
        assertEquals(customerId, event.getAggregateId());
        verify(kafkaTemplate).send(any(Message.class));
    }

    @Test
    @DisplayName("Should handle null event gracefully")
    void testPublishNullEvent() {
        boolean result = eventPublisher.publishEvent(null);
        assertFalse(result, "Should return false for null event");
    }
}
