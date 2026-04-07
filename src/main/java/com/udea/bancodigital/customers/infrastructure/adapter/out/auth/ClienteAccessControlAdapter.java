package com.udea.bancodigital.customers.infrastructure.adapter.out.auth;

import com.udea.bancodigital.auth.infrastructure.config.JwtProvider;
import com.udea.bancodigital.customers.domain.exception.ClienteNoAutorizadoException;
import com.udea.bancodigital.customers.domain.port.out.ClienteAccessControlPort;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClienteAccessControlAdapter implements ClienteAccessControlPort {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_CAJERO = "ROLE_CAJERO";
    private static final String ROLE_CLIENTE = "ROLE_CLIENTE";

    private final JwtProvider jwtProvider;
    private final HttpServletRequest request;

    @Override
    public void validateCanView(UUID clienteId) {
        if (canManageClientes()) {
            return;
        }

        if (hasRole(ROLE_CLIENTE) && clienteId.equals(extractClienteId())) {
            return;
        }

        throw new ClienteNoAutorizadoException(clienteId);
    }

    @Override
    public boolean canManageClientes() {
        return hasRole(ROLE_ADMIN) || hasRole(ROLE_CAJERO);
    }

    private boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> role.equals(authority.getAuthority()));
    }

    private UUID extractClienteId() {
        String bearer = request.getHeader("Authorization");
        if (bearer == null || !bearer.startsWith("Bearer ")) {
            return null;
        }

        return jwtProvider.extractClienteId(bearer.substring(7));
    }
}
