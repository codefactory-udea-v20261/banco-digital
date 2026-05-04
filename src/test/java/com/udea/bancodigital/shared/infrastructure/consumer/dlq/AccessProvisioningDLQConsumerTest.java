package com.udea.bancodigital.shared.infrastructure.consumer.dlq;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith(MockitoExtension.class)
class AccessProvisioningDLQConsumerTest {

    @InjectMocks
    private AccessProvisioningDLQConsumer consumer;

    @Test
    void consumeDLQEvent_ShouldLogAndNotThrow() {
        Map<String, Object> event = Map.of(
                "clienteId", "123",
                "retryCount", 5,
                "failureReason", "Test failure"
        );

        assertThatCode(() -> consumer.consumeDLQEvent(event))
                .doesNotThrowAnyException();
    }
}
