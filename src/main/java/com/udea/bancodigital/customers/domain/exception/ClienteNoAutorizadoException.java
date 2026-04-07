package com.udea.bancodigital.customers.domain.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ClienteNoAutorizadoException extends BusinessException {

    public ClienteNoAutorizadoException(UUID id) {
        super(
                "CLIENTE_NO_AUTORIZADO",
                "No tiene permisos para consultar el cliente con ID: " + id,
                HttpStatus.FORBIDDEN
        );
    }
}
