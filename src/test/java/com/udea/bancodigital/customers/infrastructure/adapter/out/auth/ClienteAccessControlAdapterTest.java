package com.udea.bancodigital.customers.infrastructure.adapter.out.auth;

import com.udea.bancodigital.auth.infrastructure.config.JwtProvider;
import com.udea.bancodigital.customers.domain.exception.ClienteNoAutorizadoException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClienteAccessControlAdapterTest {

    private final JwtProvider jwtProvider = mock(JwtProvider.class);
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final ClienteAccessControlAdapter adapter = new ClienteAccessControlAdapter(jwtProvider, request);

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deberiaPermitirConsultaCuandoUsuarioEsCajero() {
        setAuthentication("ROLE_CAJERO");

        assertDoesNotThrow(() -> adapter.validateCanView(UUID.randomUUID()));
    }

    @Test
    void deberiaPermitirConsultaCuandoClienteConsultaSuPropioPerfil() {
        UUID clienteId = UUID.randomUUID();
        setAuthentication("ROLE_CLIENTE");
        when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
        when(jwtProvider.extractClienteId("token-valido")).thenReturn(clienteId);

        assertDoesNotThrow(() -> adapter.validateCanView(clienteId));
    }

    @Test
    void deberiaLanzarExcepcionCuandoClienteConsultaOtroPerfil() {
        UUID clienteId = UUID.randomUUID();
        UUID otroClienteId = UUID.randomUUID();
        setAuthentication("ROLE_CLIENTE");
        when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
        when(jwtProvider.extractClienteId("token-valido")).thenReturn(clienteId);

        assertThrows(ClienteNoAutorizadoException.class, () -> adapter.validateCanView(otroClienteId));
    }

    private void setAuthentication(String role) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "user",
                null,
                List.of(new SimpleGrantedAuthority(role))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
