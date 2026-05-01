package com.udea.bancodigital.transactions.domain.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class CuentaTransaccionException extends BusinessException {
    public CuentaTransaccionException(String cuenta) {
        super("CUENTA_INVALIDA", "La cuenta "+cuenta+" es invalida", HttpStatus.NOT_FOUND);
    }
    public static CuentaTransaccionException noEncontrada(UUID id) {
        return new CuentaTransaccionException("La cuenta con ID " + id + " no existe o no está activa.");
    }
}
