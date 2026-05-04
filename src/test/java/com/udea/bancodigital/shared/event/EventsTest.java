package com.udea.bancodigital.shared.event;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

class EventsTest {

    @Test
    void testDomainEvent() {
        DomainEvent event = DomainEvent.createEvent("TEST", "agg-1", "test-service");
        event.setSagaId("saga-1");

        assertThat(event.getEventType()).isEqualTo("TEST");
        assertThat(event.getAggregateId()).isEqualTo("agg-1");
        assertThat(event.getSourceService()).isEqualTo("test-service");
        assertThat(event.getSagaId()).isEqualTo("saga-1");

        DomainEvent sagaEvent = DomainEvent.createEventWithSaga("TEST_SAGA", "agg-2", "saga-2", "test-service");
        assertThat(sagaEvent.getSagaId()).isEqualTo("saga-2");
    }

    @Test
    void testCustomerCreatedEvent() {
        CustomerCreatedEvent event = CustomerCreatedEvent.from(
                "cust-1", "test@test.com", "John Doe", "CC", "123", "555-1234", "user-1");
        
        assertThat(event.getCustomerId()).isEqualTo("cust-1");
        assertThat(event.getEmail()).isEqualTo("test@test.com");
        assertThat(event.getFullName()).isEqualTo("John Doe");
        assertThat(event.getDocumentType()).isEqualTo("CC");
        assertThat(event.getDocumentNumber()).isEqualTo("123");
        assertThat(event.getPhone()).isEqualTo("555-1234");
        assertThat(event.getUserId()).isEqualTo("user-1");
        assertThat(event.getEventType()).isEqualTo("CustomerCreated");
    }

    @Test
    void testTransactionCompletedEvent() {
        TransactionCompletedEvent event = TransactionCompletedEvent.from(
                "txn-1", "acc-1", "acc-2", new BigDecimal("100"), "USD", "TRANSFER", "COMPLETED", "Test", "user-1");

        assertThat(event.getTransactionId()).isEqualTo("txn-1");
        assertThat(event.getFromAccountId()).isEqualTo("acc-1");
        assertThat(event.getToAccountId()).isEqualTo("acc-2");
        assertThat(event.getAmount()).isEqualTo(new BigDecimal("100"));
        assertThat(event.getCurrency()).isEqualTo("USD");
        assertThat(event.getTransactionType()).isEqualTo("TRANSFER");
        assertThat(event.getStatus()).isEqualTo("COMPLETED");
        assertThat(event.getDescription()).isEqualTo("Test");
    }
}
