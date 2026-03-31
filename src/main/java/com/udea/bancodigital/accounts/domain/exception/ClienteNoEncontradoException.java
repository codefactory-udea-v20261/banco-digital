package com.udea.bancodigital.accounts.domain.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ClienteNoEncontradoException extends BusinessException {
    public ClienteNoEncontradoException(String message) {
        super("CLIENTE_NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }
}
