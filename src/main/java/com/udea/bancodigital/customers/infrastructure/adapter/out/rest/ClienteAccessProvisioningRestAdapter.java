package com.udea.bancodigital.customers.infrastructure.adapter.out.rest;

import com.udea.bancodigital.customers.domain.port.out.ClienteAccessProvisioningPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Adaptador REST que implementa ClienteAccessProvisioningPort
 * para comunicarse con el Identity Service.
 */
@Slf4j
@Component
@org.springframework.context.annotation.Primary
@RequiredArgsConstructor
public class ClienteAccessProvisioningRestAdapter implements ClienteAccessProvisioningPort {

    private final RestTemplate restTemplate;

    @Value("${services.identity-service.url:http://identity:8081}")
    private String identityServiceUrl;

    @Override
    public boolean existsByEmail(String email) {
        try {
            String url = identityServiceUrl + "/api/v1/internal/users/exists?email=" + email;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (boolean) response.getBody().getOrDefault("exists", false);
            }
            return false;
        } catch (RestClientException e) {
            log.warn("Error checking if email exists in Identity Service: {}", e.getMessage());
            // En caso de error, asumir que no existe para permitir creación
            return false;
        }
    }

    @Override
    public void provisionAccess(UUID clienteId, String email) {
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
            } else {
                log.info("Successfully provisioned access for client {} with email {}", 
                    clienteId, email);
            }
        } catch (RestClientException e) {
            log.error("Error provisioning access for client {} with email {}: {}", 
                clienteId, email, e.getMessage());
            throw new RuntimeException("Failed to provision access in Identity Service", e);
        }
    }
}
