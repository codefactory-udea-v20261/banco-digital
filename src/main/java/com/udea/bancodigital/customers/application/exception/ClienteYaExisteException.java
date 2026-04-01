package com.udea.bancodigital.customers.application.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ClienteYaExisteException extends BusinessException {

    public ClienteYaExisteException() {
        super(
                "CLIENTE_YA_EXISTE",
                "El cliente ya se encuentra registrado",
                HttpStatus.CONFLICT
        );
    }
}
