package com.udea.bancodigital.shared.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

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

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        eventPublisher = new EventPublisher(kafkaTemplate);
    }

    @Test
    @DisplayName("Should publish CustomerCreatedEvent successfully")
    void testPublishCustomerCreatedEvent() {
        // Arrange
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

        // Act
        boolean result = eventPublisher.publishEvent(event);

        // Assert
        assertTrue(result, "Event should be published successfully");
        assertNotNull(event.getEventId(), "Event ID should not be null");
        assertEquals("CustomerCreated", event.getEventType());
        assertEquals(customerId, event.getAggregateId());
        verify(kafkaTemplate).send(any(Message.class));
    }

    @Test
    @DisplayName("Should publish TransactionCompletedEvent successfully")
    void testPublishTransactionCompletedEvent() {
        // Arrange
        String transactionId = UUID.randomUUID().toString();
        String fromAccountId = "ACC-001";
        String toAccountId = "ACC-002";
        BigDecimal amount = new BigDecimal("1000.00");
        String currency = "COP";
        String transactionType = "TRANSFER";
        String status = "COMPLETED";
        String description = "Payment to supplier";
        String userId = "user-123";

        TransactionCompletedEvent event = TransactionCompletedEvent.from(
                transactionId, fromAccountId, toAccountId, amount, currency,
                transactionType, status, description, userId
        );

        CompletableFuture<SendResult<String, DomainEvent>> future = CompletableFuture.completedFuture(mock(SendResult.class));
        when(kafkaTemplate.send(any(Message.class))).thenReturn(future);

        // Act
        boolean result = eventPublisher.publishEvent(event);

        // Assert
        assertTrue(result, "Event should be published successfully");
        assertEquals("TransactionCompleted", event.getEventType());
        assertEquals(transactionId, event.getAggregateId());
        assertEquals(amount, event.getAmount());
    }

    @Test
    @DisplayName("Should handle null event gracefully")
    void testPublishNullEvent() {
        // Act
        boolean result = eventPublisher.publishEvent(null);

        // Assert
        assertFalse(result, "Should return false for null event");
    }

    @Test
    @DisplayName("Should send failed event to DLQ")
    void testPublishEventToDLQ() {
        // Arrange
        String customerId = UUID.randomUUID().toString();
        DomainEvent event = DomainEvent.createEvent("CustomerCreated", customerId, "core-banking-service");
        String reason = "Consumer processing failed";

        // Act
        eventPublisher.publishEventToDLQ(event, reason);

        // Assert - verify kafkaTemplate.send was called for DLQ
        verify(kafkaTemplate).send(any(Message.class));
    }

    @Test
    @DisplayName("Should create event with correct structure")
    void testEventStructure() {
        // Arrange & Act
        String customerId = "customer-123";
        DomainEvent event = DomainEvent.createEvent(
                "CustomerCreated",
                customerId,
                "core-banking-service"
        );

        // Assert
        assertNotNull(event.getEventId());
        assertEquals("CustomerCreated", event.getEventType());
        assertEquals(customerId, event.getAggregateId());
        assertEquals(customerId, event.getCorrelationId());
        assertEquals("core-banking-service", event.getSourceService());
        assertEquals(1, event.getVersion());
        assertNotNull(event.getOccurredAt());
        assertTrue(event.getOccurredAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Should create event with saga ID")
    void testEventWithSagaId() {
        // Arrange & Act
        String customerId = "customer-123";
        String sagaId = "saga-456";
        DomainEvent event = DomainEvent.createEventWithSaga(
                "CustomerCreated",
                customerId,
                sagaId,
                "core-banking-service"
        );

        // Assert
        assertEquals(sagaId, event.getSagaId());
        assertEquals(customerId, event.getAggregateId());
    }
}
