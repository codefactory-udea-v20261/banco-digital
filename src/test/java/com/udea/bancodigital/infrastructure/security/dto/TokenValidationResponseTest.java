package com.udea.bancodigital.infrastructure.security.dto;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class TokenValidationResponseTest {

    @Test
    void testGettersAndSetters() {
        TokenValidationResponse response = new TokenValidationResponse();
        
        response.setActive(true);
        response.setSub("user@test.com");
        response.setAuthorities(List.of("ROLE_USER"));
        response.setClienteId("client-123");
        response.setUid("uid-456");

        assertThat(response.isActive()).isTrue();
        assertThat(response.getSub()).isEqualTo("user@test.com");
        assertThat(response.getAuthorities()).containsExactly("ROLE_USER");
        assertThat(response.getClienteId()).isEqualTo("client-123");
        assertThat(response.getUid()).isEqualTo("uid-456");
    }
}
