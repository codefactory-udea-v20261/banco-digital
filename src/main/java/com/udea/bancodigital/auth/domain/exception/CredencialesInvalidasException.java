package com.udea.bancodigital.auth.domain.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * HU11 — Credenciales incorrectas. El mensaje es genérico intencionalmente
 * para no revelar si el usuario existe (seguridad OWASP).
 */
public class CredencialesInvalidasException extends BusinessException {

    public CredencialesInvalidasException() {
        super("CREDENCIALES_INVALIDAS",
              "Usuario o contraseña incorrectos",
              HttpStatus.UNAUTHORIZED);
    }
}
