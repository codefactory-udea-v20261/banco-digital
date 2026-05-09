package com.udea.bancodigital.customers.infrastructure.adapter.out.rest;

import com.udea.bancodigital.customers.domain.port.out.ClienteAccessProvisioningPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Adaptador REST resiliente para ClienteAccessProvisioningPort
 * con Circuit Breaker, Retry y fallback a Kafka queue.
 *
 * Patrón: Resilience4j + Event Sourcing
 * - Si Identity Service está disponible: call sincrónico
 * - Si Identity Service cae: queue evento y continuar
 * - Circuit breaker abre después de 50% fallos en ventana 10 requests
 * - Retry con backoff exponencial: 1s, 2s, 4s
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteAccessProvisioningCircuitBreakerAdapter implements ClienteAccessProvisioningPort {
    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    @Value("${services.identity-service.url:http://identity:8081}")
    private String identityServiceUrl;

    /**
     * Check if email exists, with circuit breaker protection.
     * Fallback: assume email doesn't exist (allow provisioning attempt)
     */
    @Override
    @CircuitBreaker(name = "identity-service", fallbackMethod = "existsByEmailFallback")
    @Retry(name = "identity-service")
    public boolean existsByEmail(String email) {
        log.debug("Checking if email exists in Identity Service: {}", email);

        try {
            String url = identityServiceUrl + "/api/v1/internal/users/exists?email=" + email;

            // FIX Sonar S3740: usar ParameterizedTypeReference en lugar de raw type Map
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                boolean exists = (boolean) response.getBody().getOrDefault("exists", false);
                log.debug("Email {} exists in Identity Service: {}", email, exists);
                return exists;
            }

            log.warn("Unexpected response from Identity Service for email check: {}", response.getStatusCode());
            return false;

        } catch (RestClientException e) {
            log.warn("Error checking if email exists in Identity Service: {}", e.getMessage());
            throw e; // Let circuit breaker handle it
        }
    }

    /**
     * Fallback for existsByEmail when Identity Service is down.
     * Returns false to allow further processing (optimistic approach).
     */
    private boolean existsByEmailFallback(String email, Exception e) {
        log.warn("Identity Service unavailable for email check (circuit breaker). "
                + "Assuming email {} doesn't exist. Error: {}", email, e.getMessage());
        return false;
    }

    /**
     * Provision client access in Identity Service.
     * Fallback: queue evento para procesamiento asincrónico cuando Identity está
     * down.
     */
    @Override
    @CircuitBreaker(name = "identity-service", fallbackMethod = "provisionAccessFallback")
    @Retry(name = "identity-service")
    public void provisionAccess(UUID clienteId, String email) {
        log.debug("Provisioning access for client {} with email {}", clienteId, email);

        try {
            String url = identityServiceUrl + "/api/v1/internal/users/provision-client-access";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("clienteId", clienteId.toString());
            requestBody.put("email", email);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Void> response = restTemplate.postForEntity(url, entity, Void.class);

            if (response.getStatusCode().isError()) {
                log.error("Failed to provision access for client {} with email {}: HTTP {}",
                        clienteId, email, response.getStatusCode());

                // FIX Sonar S112: usar RestClientException (específica de Spring) en lugar de
                // RuntimeException
                throw new RestClientException(
                        "Identity Service returned error: " + response.getStatusCode());
            } else {
                log.info("Successfully provisioned access for client {} with email {}", clienteId, email);
            }

        } catch (RestClientException e) {
            log.error("RestClient error provisioning access for client {} with email {}: {}",
                    clienteId, email, e.getMessage());
            throw e;
        }
    }

    /**
     * Fallback when Identity Service is down or circuit breaker is open.
     * Queue evento para procesamiento asincrónico (event sourcing pattern).
     */
    private void provisionAccessFallback(UUID clienteId, String email, Exception e) {
        log.warn("Identity Service unavailable (circuit breaker OPEN or exhausted retries). "
                + "Queueing provision-access event for clienteId={}, email={}. Error: {}",
                clienteId, email, e.getMessage());

        try {
            Map<String, Object> pendingEvent = new HashMap<>();
            pendingEvent.put("eventType", "ClienteAccessProvisioningPending");
            pendingEvent.put("clienteId", clienteId.toString());
            pendingEvent.put("email", email);
            pendingEvent.put("timestamp", Instant.now().toString());
            pendingEvent.put("retryCount", 0);

            kafkaTemplate.send("cliente-access-provisioning-pending", clienteId.toString(), pendingEvent);
            log.info("Queued pending provisioning event for clienteId={}", clienteId);

        } catch (Exception kafkaError) {
            log.error("Failed to queue fallback event for clienteId={}, email={}: {}",
                    clienteId, email, kafkaError.getMessage());
        }
    }

    /**
     * Get circuit breaker status (for monitoring/health endpoints).
     */
    public Map<String, Object> getCircuitBreakerStatus() {
        return Map.of(
                "circuitBreakerName", "identity-service",
                "identityServiceUrl", identityServiceUrl,
                "note", "Use @EnableActuator to expose /actuator/health/identity-service");
    }
}
