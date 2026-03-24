package com.udea.bancodigital.customers.application.exception;


public class ClienteYaExisteException extends RuntimeException {

    public ClienteYaExisteException() {
        super("El cliente ya se encuentra registrado");
    }

    public ClienteYaExisteException(String message) {
        super(message);
    }
}
