package com.udea.bancodigital.accounts.infrastructure.adapter.out;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.udea.bancodigital.accounts.domain.port.out.AuthServicePort;
import com.udea.bancodigital.auth.infrastructure.config.JwtProvider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthServiceAdapter implements AuthServicePort {
    private final JwtProvider jwtProvider;
    private final HttpServletRequest request;

    @Override
    public UUID getClienteId() {

        String bearer = request.getHeader("Authorization");

        if (bearer == null || !bearer.startsWith("Bearer ")) {
            throw new RuntimeException("Token no válido o ausente");
        }

        String token = bearer.substring(7);

        return jwtProvider.extractClienteId(token);
    }
}
