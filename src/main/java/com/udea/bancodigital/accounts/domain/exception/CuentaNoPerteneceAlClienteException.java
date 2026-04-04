package com.udea.bancodigital.accounts.domain.exception;

import java.util.UUID;

public class CuentaNoPerteneceAlClienteException extends RuntimeException {

    public CuentaNoPerteneceAlClienteException(UUID cuentaId) {
        super("La cuenta con id " + cuentaId + " no pertenece al cliente autenticado");
    }
}