package com.udea.bancodigital.shared.infrastructure.consumer.pending;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PendingAccessProvisioningConsumerTest {

    @Mock
    private KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    @InjectMocks
    private PendingAccessProvisioningConsumer consumer;

    @Test
    void consumePendingEvent_Success() {
        Map<String, Object> event = new HashMap<>();
        event.put("clienteId", "123");
        event.put("email", "test@example.com");
        
        assertDoesNotThrow(() -> consumer.consumePendingEvent(event));
    }

    @Test
    void consumePendingEvent_TriggersRetry() {
        // Force failure by using an immutable map or one that throws on get
        Map<String, Object> failingEvent = new HashMap<String, Object>() {
            @Override
            public Object get(Object key) {
                if ("clienteId".equals(key)) return "123";
                if ("email".equals(key)) throw new RuntimeException("Forced failure");
                return super.get(key);
            }
            
            @Override
            public Object put(String key, Object value) {
                if ("retryCount".equals(key)) return super.put(key, value);
                return super.put(key, value);
            }
        };
        failingEvent.put("retryCount", 0);

        when(kafkaTemplate.send(anyString(), anyString(), anyMap())).thenReturn(CompletableFuture.completedFuture(null));

        consumer.consumePendingEvent(failingEvent);

        verify(kafkaTemplate).send(eq("cliente-access-provisioning-pending"), anyString(), anyMap());
        assertEquals(1, failingEvent.get("retryCount"));
    }

    @Test
    void consumePendingEvent_MovesToDLQ() {
        Map<String, Object> event = new HashMap<>();
        event.put("clienteId", "123");
        event.put("retryCount", 5); // Max retries
        
        // Force failure in try block
        Map<String, Object> failingEvent = new HashMap<String, Object>(event) {
            @Override
            public Object get(Object key) {
                if ("email".equals(key)) throw new RuntimeException("Forced failure");
                return super.get(key);
            }
        };

        when(kafkaTemplate.send(anyString(), anyString(), anyMap())).thenReturn(CompletableFuture.completedFuture(null));

        consumer.consumePendingEvent(failingEvent);

        verify(kafkaTemplate).send(eq("cliente-access-provisioning-dlq"), eq("123"), anyMap());
    }

    @Test
    void getStats_ReturnsCorrectInfo() {
        Map<String, Object> stats = consumer.getStats();
        assertEquals("cliente-access-provisioning-pending", stats.get("topic"));
        assertEquals(5, stats.get("maxRetries"));
    }
}
