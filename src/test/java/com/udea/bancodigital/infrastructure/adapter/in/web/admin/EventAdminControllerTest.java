package com.udea.bancodigital.infrastructure.adapter.in.web.admin;

import com.udea.bancodigital.shared.event.DomainEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventAdminControllerTest {

    @Mock
    private KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private EventAdminController eventAdminController;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin-user");
    }

    @Test
    void replayEvents_ReturnsNotImplemented() {
        ResponseEntity<EventAdminController.ReplayResponse> response = 
                eventAdminController.replayEvents("2023-01-01", "2023-01-02");
        
        assertEquals(HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());
    }

    @Test
    void publishTestEvent_Success() {
        when(kafkaTemplate.send(anyString(), anyString(), any(DomainEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        ResponseEntity<String> response = eventAdminController.publishTestEvent();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Test event published"));
    }

    @Test
    void publishTestEvent_RateLimitExceeded() {
        when(kafkaTemplate.send(anyString(), anyString(), any(DomainEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Call 5 times to hit limit
        for (int i = 0; i < 5; i++) {
            eventAdminController.publishTestEvent();
        }

        ResponseEntity<String> response = eventAdminController.publishTestEvent();
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertTrue(response.getBody().contains("Rate limit exceeded"));
    }

    @Test
    void checkHealth_Success() {
        when(kafkaTemplate.send(anyString(), anyString(), any(DomainEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        ResponseEntity<String> response = eventAdminController.checkHealth();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Kafka is healthy"));
    }

    @Test
    void checkHealth_Failure() {
        CompletableFuture<Object> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka Down"));
        
        doReturn(future).when(kafkaTemplate).send(anyString(), anyString(), any(DomainEvent.class));

        ResponseEntity<String> response = eventAdminController.checkHealth();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertTrue(response.getBody().contains("Kafka is unavailable"));
    }
}
