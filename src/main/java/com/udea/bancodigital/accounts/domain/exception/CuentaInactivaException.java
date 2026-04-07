package com.udea.bancodigital.accounts.domain.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class CuentaInactivaException extends BusinessException {

    public CuentaInactivaException(UUID cuentaId) {
        super(
                "CUENTA_INACTIVA",
                "La cuenta " + cuentaId + " no está activa",
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }
}
