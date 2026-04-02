package com.udea.bancodigital.auth.infrastructure.adapter.out;

import com.udea.bancodigital.auth.domain.model.Rol;
import com.udea.bancodigital.auth.domain.model.Usuario;
import com.udea.bancodigital.auth.domain.port.out.PasswordEncoderPort;
import com.udea.bancodigital.auth.domain.port.out.UsuarioRepositoryPort;
import com.udea.bancodigital.customers.domain.port.out.ClienteAccessProvisioningPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClienteAccessProvisioningAdapter implements ClienteAccessProvisioningPort {

    private static final short CLIENT_ROLE_ID = 3;
    private static final String CLIENT_ROLE_NAME = "CLIENTE";

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    @Value("${app.security.default-client-password:Temp1234!}")
    private String defaultClientPassword;

    @Override
    public boolean existsByEmail(String email) {
        return usuarioRepositoryPort.existsByUsername(email);
    }

    @Override
    public void provisionAccess(UUID clienteId, String email) {
        Usuario usuarioCliente = Usuario.builder()
                .id(UUID.randomUUID())
                .clienteId(clienteId)
                .correo(email)
                .clave(passwordEncoderPort.encode(defaultClientPassword))
                .activo(true)
                .bloqueado(false)
                .intentosFallidos(0)
                .roles(Set.of(Rol.builder()
                        .id(CLIENT_ROLE_ID)
                        .nombre(CLIENT_ROLE_NAME)
                        .build()))
                .build();

        usuarioRepositoryPort.save(usuarioCliente);
    }
}
