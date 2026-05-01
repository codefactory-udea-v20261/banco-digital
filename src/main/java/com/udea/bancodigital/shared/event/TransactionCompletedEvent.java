package com.udea.bancodigital.shared.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Event emitted when a transaction is completed.
 * This event is published to audit and reporting services for compliance and analytics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class TransactionCompletedEvent extends DomainEvent {

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("from_account_id")
    private String fromAccountId;

    @JsonProperty("to_account_id")
    private String toAccountId;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("transaction_type")
    private String transactionType;

    @JsonProperty("status")
    private String status;

    @JsonProperty("description")
    private String description;

    public static TransactionCompletedEvent from(
            String transactionId,
            String fromAccountId,
            String toAccountId,
            BigDecimal amount,
            String currency,
            String transactionType,
            String status,
            String description,
            String userId) {
        return TransactionCompletedEvent.builder()
                .transactionId(transactionId)
                .fromAccountId(fromAccountId)
                .toAccountId(toAccountId)
                .amount(amount)
                .currency(currency)
                .transactionType(transactionType)
                .status(status)
                .description(description)
                .eventId(UUID.randomUUID().toString())
                .eventType("TransactionCompleted")
                .aggregateId(transactionId)
                .correlationId(transactionId)
                .sourceService("core-banking-service")
                .occurredAt(java.time.LocalDateTime.now())
                .version(1)
                .userId(userId)
                .build();
    }
}
