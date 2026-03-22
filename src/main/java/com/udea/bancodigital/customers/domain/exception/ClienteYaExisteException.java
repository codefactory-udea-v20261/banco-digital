package com.udea.bancodigital.customers.domain.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ClienteYaExisteException extends BusinessException {

    public ClienteYaExisteException(String email) {
        super("CLIENTE_ALREADY_EXISTS",
              "Ya existe un cliente registrado con el email: " + email,
              HttpStatus.CONFLICT);
    }

    public ClienteYaExisteException(String campo, String valor) {
        super("CLIENTE_ALREADY_EXISTS",
              "Ya existe un cliente con " + campo + ": " + valor,
              HttpStatus.CONFLICT);
    }
}
