package com.udea.bancodigital.accounts.domain.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class CuentaNoPerteneceAlClienteException extends BusinessException {

    public CuentaNoPerteneceAlClienteException(UUID cuentaId) {
        super(
                "CUENTA_NO_AUTORIZADA",
                "La cuenta no pertenece al cliente",
                HttpStatus.FORBIDDEN
        );
    }
}