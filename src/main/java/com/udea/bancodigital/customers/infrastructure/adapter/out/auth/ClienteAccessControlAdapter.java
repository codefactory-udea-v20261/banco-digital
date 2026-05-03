package com.udea.bancodigital.customers.infrastructure.adapter.out.auth;

import com.udea.bancodigital.customers.domain.exception.ClienteNoAutorizadoException;
import com.udea.bancodigital.customers.domain.port.out.ClienteAccessControlPort;
import com.udea.bancodigital.shared.security.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ClienteAccessControlAdapter implements ClienteAccessControlPort {

    private static final String PERM_MANAGE_CLIENTS = "PERM_MANAGE_CLIENTS";
    private static final String PERM_READ_OWN_PROFILE = "PERM_READ_OWN_PROFILE";

    @Override
    public void validateCanView(UUID clienteId) {
        if (canManageClientes()) {
            return;
        }

        if (hasAuthority(PERM_READ_OWN_PROFILE) && clienteId.equals(extractClienteId())) {
            return;
        }

        throw new ClienteNoAutorizadoException(clienteId);
    }

    @Override
    public boolean canManageClientes() {
        return hasAuthority(PERM_MANAGE_CLIENTS);
    }

    private boolean hasAuthority(String authorityName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authorityName.equals(authority.getAuthority()));
    }

    private UUID extractClienteId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser principal)) {
            return null;
        }

        return principal.clienteId();
    }
}
