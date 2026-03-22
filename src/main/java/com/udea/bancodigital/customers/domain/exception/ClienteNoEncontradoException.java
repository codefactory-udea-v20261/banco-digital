package com.udea.bancodigital.customers.domain.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ClienteNoEncontradoException extends BusinessException {

    public ClienteNoEncontradoException(UUID id) {
        super("CLIENTE_NOT_FOUND",
              "No se encontró un cliente con ID: " + id,
              HttpStatus.NOT_FOUND);
    }

    public ClienteNoEncontradoException(String cedula) {
        super("CLIENTE_NOT_FOUND",
              "No se encontró un cliente con cédula: " + cedula,
              HttpStatus.NOT_FOUND);
    }
}
