package com.udea.bancodigital.customers.shared.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ClienteYaExisteException extends BusinessException {

    public ClienteYaExisteException(String campo, String valor) {
        super(
                "CLIENTE_YA_EXISTE",
                "Ya existe un cliente con " + campo + ": " + valor,
                HttpStatus.CONFLICT
        );
    }
}
