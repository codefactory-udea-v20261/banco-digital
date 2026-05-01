package com.udea.bancodigital.infrastructure.security;

import com.udea.bancodigital.infrastructure.security.dto.TokenValidationResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "identity-service", url = "${services.identity.url}")
public interface IdentityServiceClient {

    @PostMapping("/api/v1/auth/validate-token")
    @CircuitBreaker(name = "identity-service", fallbackMethod = "fallbackValidateToken")
    TokenValidationResponse validateToken(@RequestBody String token);

    default TokenValidationResponse fallbackValidateToken(String token, Throwable t) {
        // Log the error
        // Return a default "invalid" response
        TokenValidationResponse response = new TokenValidationResponse();
        response.setActive(false);
        return response;
    }
}
