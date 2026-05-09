package com.udea.bancodigital.customers.infrastructure.adapter.out.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteAccessProvisioningCircuitBreakerAdapterTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    @InjectMocks
    private ClienteAccessProvisioningCircuitBreakerAdapter adapter;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adapter, "identityServiceUrl", "http://test-identity");
    }

    @Test
    void existsByEmail_Success() {
        Map<String, Object> body = Collections.singletonMap("exists", true);
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(body, HttpStatus.OK);

        // FIX: la clase usa exchange() con ParameterizedTypeReference, no
        // getForEntity()
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class))).thenReturn(response);

        boolean result = adapter.existsByEmail("test@example.com");

        assertTrue(result);
    }

    @Test
    void existsByEmail_Error_ThrowsException() {
        // FIX: la clase usa exchange() con ParameterizedTypeReference, no
        // getForEntity()
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class))).thenThrow(new RestClientException("Error"));

        assertThrows(RestClientException.class, () -> adapter.existsByEmail("test@example.com"));
    }

    @Test
    void provisionAccess_Success() {
        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.CREATED);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(response);

        assertDoesNotThrow(() -> adapter.provisionAccess(UUID.randomUUID(), "test@example.com"));
    }

    @Test
    void getCircuitBreakerStatus_ReturnsInfo() {
        Map<String, Object> status = adapter.getCircuitBreakerStatus();
        assertEquals("identity-service", status.get("circuitBreakerName"));
    }

}
