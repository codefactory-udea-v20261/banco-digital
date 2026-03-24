package com.udea.bancodigital.accounts.application.exception;

public class DatosIncompletosException extends RuntimeException {

    public DatosIncompletosException() {
        super("La información proporcionada es insuficiente");
    }

    public DatosIncompletosException(String message) {
        super(message);
    }
}
