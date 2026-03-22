package com.udea.bancodigital.auth.domain.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * HU11 — Se activa por el Trigger de BD tras 3 intentos fallidos (Sprint 3).
 */
public class CuentaBloqueadaException extends BusinessException {

    public CuentaBloqueadaException(ZonedDateTime bloqueadoHasta) {
        super("CUENTA_BLOQUEADA",
              "La cuenta está bloqueada hasta: " +
                      bloqueadoHasta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
              HttpStatus.FORBIDDEN);
    }
}
