package com.udea.bancodigital.accounts.domain.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ClienteInactivoException extends BusinessException {

    public ClienteInactivoException(UUID clienteId) {
        super(
                "CLIENTE_INACTIVO",
                "No se puede crear la cuenta: El cliente con ID " + clienteId + " se encuentra inactivo.",
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }
}
