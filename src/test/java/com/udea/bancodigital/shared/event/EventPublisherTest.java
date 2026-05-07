package com.udea.bancodigital.shared.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventPublisherTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EventPublisher eventPublisher;

    private String validEncryptionKey;

    @BeforeEach
    void setUp() {
        // Generate a 256-bit key (32 bytes)
        byte[] key = new byte[32];
        new java.security.SecureRandom().nextBytes(key);
        validEncryptionKey = Base64.getEncoder().encodeToString(key);
        ReflectionTestUtils.setField(eventPublisher, "encryptionKey", validEncryptionKey);
    }

    @Test
    void publishEvent_Success() throws Exception {
        DomainEvent event = new DomainEvent() {
            @Override public String getEventType() { return "TEST_EVENT"; }
            @Override public String getAggregateId() { return "123"; }
        };
        event.setEventId(UUID.randomUUID().toString());

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"test\":\"data\"}");
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any())).thenReturn(1);

        boolean result = eventPublisher.publishEvent(event);

        assertTrue(result);
        verify(jdbcTemplate).update(contains("INSERT INTO outbox_events"), any(), any(), any(), any(), any(), any());
    }

    @Test
    void publishEvent_NullEvent_ReturnsFalse() {
        assertFalse(eventPublisher.publishEvent(null));
    }

    @Test
    void publishEvent_MissingEncryptionKey_ThrowsException() {
        ReflectionTestUtils.setField(eventPublisher, "encryptionKey", null);
        DomainEvent event = new DomainEvent() {
            @Override public String getEventType() { return "TEST_EVENT"; }
            @Override public String getAggregateId() { return "123"; }
        };
        
        assertThrows(RuntimeException.class, () -> eventPublisher.publishEvent(event));
    }

    @Test
    void publishEvent_JdbcError_ThrowsException() throws Exception {
        DomainEvent event = new DomainEvent() {
            @Override public String getEventType() { return "TEST_EVENT"; }
            @Override public String getAggregateId() { return "123"; }
        };
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("DB Error"));

        assertThrows(RuntimeException.class, () -> eventPublisher.publishEvent(event));
    }
}
