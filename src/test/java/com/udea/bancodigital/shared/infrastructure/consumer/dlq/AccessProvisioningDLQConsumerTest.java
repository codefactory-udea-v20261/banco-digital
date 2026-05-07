package com.udea.bancodigital.shared.infrastructure.consumer.dlq;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class AccessProvisioningDLQConsumerTest {

    @InjectMocks
    private AccessProvisioningDLQConsumer consumer;

    @Test
    void consumeDLQEvent_Success() {
        Map<String, Object> event = new HashMap<>();
        event.put("clienteId", "123");
        event.put("retryCount", 5);
        event.put("failureReason", "Test error");
        
        assertDoesNotThrow(() -> consumer.consumeDLQEvent(event));
    }
}
