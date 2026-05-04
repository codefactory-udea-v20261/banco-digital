package com.udea.bancodigital.customers.infrastructure.adapter.out.auth;

import com.udea.bancodigital.customers.domain.exception.ClienteNoAutorizadoException;
import com.udea.bancodigital.shared.security.AuthenticatedUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClienteAccessControlAdapterTest {

    private final ClienteAccessControlAdapter adapter = new ClienteAccessControlAdapter();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deberiaPermitirConsultaCuandoUsuarioEsCajero() {
        setAuthentication("PERM_MANAGE_CLIENTS");

        assertDoesNotThrow(() -> adapter.validateCanView(UUID.randomUUID()));
    }

    @Test
    void deberiaPermitirConsultaCuandoClienteConsultaSuPropioPerfil() {
        UUID clienteId = UUID.randomUUID();
        setAuthentication("PERM_READ_OWN_PROFILE", clienteId);

        assertDoesNotThrow(() -> adapter.validateCanView(clienteId));
    }

    @Test
    void deberiaLanzarExcepcionCuandoClienteConsultaOtroPerfil() {
        UUID clienteId = UUID.randomUUID();
        UUID otroClienteId = UUID.randomUUID();
        setAuthentication("PERM_READ_OWN_PROFILE", clienteId);

        assertThrows(ClienteNoAutorizadoException.class, () -> adapter.validateCanView(otroClienteId));
    }

    private void setAuthentication(String role) {
        setAuthentication(role, null);
    }

    private void setAuthentication(String role, UUID clienteId) {
        AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "user@test.com", clienteId);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority(role))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void deberiaRetornarFalsoYNoLanzarExcepcionConAuthNula() {
        SecurityContextHolder.getContext().setAuthentication(null);
        assertThrows(ClienteNoAutorizadoException.class, () -> adapter.validateCanView(UUID.randomUUID()));
    }
}
