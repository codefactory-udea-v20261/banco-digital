package com.udea.bancodigital.infrastructure.security;

import com.udea.bancodigital.infrastructure.security.dto.TokenValidationResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdentityServiceClientTest {

    @Test
    void testFallbackValidateToken() {
        IdentityServiceClient client = new IdentityServiceClient() {
            @Override
            public TokenValidationResponse validateToken(String token) {
                return null;
            }
        };

        TokenValidationResponse response = client.fallbackValidateToken("invalid-token", new RuntimeException("Error"));

        assertThat(response).isNotNull();
        assertThat(response.isActive()).isFalse();
    }
}
