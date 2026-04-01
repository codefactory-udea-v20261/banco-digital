package com.udea.bancodigital.customers.domain.exception;

public class CampoInmutableException extends RuntimeException {
    public CampoInmutableException(String campo) {
        super("El campo '" + campo + "' no puede ser modificado");
    }
}
