package com.udea.bancodigital.customers.infrastructure.adapter.out.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteAccessProvisioningRestAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ClienteAccessProvisioningRestAdapter adapter;

    @Test
    void existsByEmail_ShouldReturnTrueIfResponseOk() {
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Map.of("exists", true), HttpStatus.OK));

        boolean exists = adapter.existsByEmail("test@test.com");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_ShouldReturnFalseOnError() {
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenThrow(new RestClientException("Connection error"));

        boolean exists = adapter.existsByEmail("test@test.com");

        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmail_ShouldReturnFalseWhenNotOk() {
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        boolean exists = adapter.existsByEmail("test@test.com");

        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmail_ShouldReturnFalseWhenBodyIsNull() {
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        boolean exists = adapter.existsByEmail("test@test.com");

        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmail_ShouldReturnFalseWhenExistsKeyMissing() {
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Map.of("other", "value"), HttpStatus.OK));

        boolean exists = adapter.existsByEmail("test@test.com");

        assertThat(exists).isFalse();
    }

    @Test
    void provisionAccess_ShouldPostSuccessfully() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        adapter.provisionAccess(UUID.randomUUID(), "test@test.com");

        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Void.class));
    }

    @Test
    void provisionAccess_ShouldThrowOnRestClientException() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                .thenThrow(new RestClientException("Connection error"));

        assertThatThrownBy(() -> adapter.provisionAccess(UUID.randomUUID(), "test@test.com"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void provisionAccess_ShouldLogErrorOnErrorStatus() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        adapter.provisionAccess(UUID.randomUUID(), "test@test.com");

        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Void.class));
    }

    @Test
    void existsByEmail_ShouldReturnFalseWhenExistsFalse() {
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Map.of("exists", false), HttpStatus.OK));

        boolean exists = adapter.existsByEmail("test@test.com");

        assertThat(exists).isFalse();
    }
}
