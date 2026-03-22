package com.udea.bancodigital.accounts.domain.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class CuentaNoEncontradaException extends BusinessException {

    public CuentaNoEncontradaException(UUID id) {
        super("CUENTA_NOT_FOUND",
              "No se encontró una cuenta con ID: " + id,
              HttpStatus.NOT_FOUND);
    }

    public CuentaNoEncontradaException(String numeroCuenta) {
        super("CUENTA_NOT_FOUND",
              "No se encontró la cuenta número: " + numeroCuenta,
              HttpStatus.NOT_FOUND);
    }
}
