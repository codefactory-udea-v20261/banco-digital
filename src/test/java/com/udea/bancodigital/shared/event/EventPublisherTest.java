package com.udea.bancodigital.shared.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventPublisherTest {

    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private EventFallbackStorage fallbackStorage;

    private EventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new EventPublisher(jdbcTemplate, objectMapper, fallbackStorage);
    }

    @Test
    void publishEvent_Success() throws Exception {
        DomainEvent event = DomainEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("TestEvent")
                .aggregateId("123")
                .build();

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any())).thenReturn(1);

        boolean result = publisher.publishEvent(event);

        assertThat(result).isTrue();
        verify(jdbcTemplate).update(anyString(), any(UUID.class), anyString(), eq("123"), eq("TestEvent"), eq("{}"));
    }

    @Test
    void publishEvent_NullEvent() {
        boolean result = publisher.publishEvent(null);
        assertThat(result).isFalse();
    }
}
