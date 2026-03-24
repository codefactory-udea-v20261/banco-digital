package com.udea.bancodigital.customers.application.exception;

public class DatosIncompletosException extends RuntimeException {

    public DatosIncompletosException() {
        super("La información proporcionada es insuficiente");
    }

    public DatosIncompletosException(String message) {
        super(message);
    }
}
