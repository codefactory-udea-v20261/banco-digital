package com.udea.bancodigital.customers.infrastructure.adapter.out.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
class ClienteAccessProvisioningRestAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ClienteAccessProvisioningRestAdapter adapter;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adapter, "identityServiceUrl", "http://test-identity");
    }

    @Test
    void existsByEmail_ReturnsTrue() {
        Map<String, Object> body = Collections.singletonMap("exists", true);
        ResponseEntity<Map> response = new ResponseEntity<>(body, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(response);

        boolean result = adapter.existsByEmail("test@example.com");

        assertTrue(result);
    }

    @Test
    void existsByEmail_ReturnsFalseOnError() {
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenThrow(new RestClientException("Connection error"));

        boolean result = adapter.existsByEmail("test@example.com");

        assertFalse(result);
    }

    @Test
    void provisionAccess_Success() {
        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.CREATED);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(response);

        assertDoesNotThrow(() -> adapter.provisionAccess(UUID.randomUUID(), "test@example.com"));
    }

    @Test
    void provisionAccess_Failure_ThrowsException() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                .thenThrow(new RestClientException("Internal Server Error"));

        assertThrows(RuntimeException.class, () -> 
                adapter.provisionAccess(UUID.randomUUID(), "test@example.com"));
    }
}
