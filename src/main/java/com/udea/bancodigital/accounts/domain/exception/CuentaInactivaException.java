package com.udea.bancodigital.accounts.domain.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class CuentaInactivaException extends BusinessException {

    public CuentaInactivaException(UUID cuentaId) {
        super(
                "La cuenta no está activa",
                "CUENTA_INACTIVA",
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }
}
