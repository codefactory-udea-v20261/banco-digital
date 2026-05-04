package com.udea.bancodigital.shared.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * Event emitted when a new customer is created in the system.
 * This event triggers downstream services (Identity, Audit) to take action.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CustomerCreatedEvent extends DomainEvent {

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("document_type")
    private String documentType;

    @JsonProperty("document_number")
    private String documentNumber;

    @JsonProperty("phone")
    private String phone;

    public static CustomerCreatedEvent from(
            String customerId,
            String email,
            String fullName,
            String documentType,
            String documentNumber,
            String phone,
            String userId) {
        return CustomerCreatedEvent.builder()
                .customerId(customerId)
                .email(email)
                .fullName(fullName)
                .documentType(documentType)
                .documentNumber(documentNumber)
                .phone(phone)
                .eventId(UUID.randomUUID().toString())
                .eventType("CustomerCreated")
                .aggregateId(customerId)
                .correlationId(customerId)
                .sourceService("core-banking-service")
                .occurredAt(java.time.LocalDateTime.now())
                .version(1)
                .userId(userId)
                .build();
    }
}
