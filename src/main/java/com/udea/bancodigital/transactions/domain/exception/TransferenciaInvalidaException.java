package com.udea.bancodigital.transactions.domain.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * HU7 — Cubre los casos: cuenta origen == destino, monto negativo o cero,
 * o cualquier otra violación de regla de negocio en transferencias.
 */
public class TransferenciaInvalidaException extends BusinessException {

    public TransferenciaInvalidaException(String motivo) {
        super("TRANSFERENCIA_INVALIDA", motivo, HttpStatus.BAD_REQUEST);
    }

    public static TransferenciaInvalidaException mismaCuenta() {
        return new TransferenciaInvalidaException(
                "La cuenta de origen y destino no pueden ser la misma");
    }

    public static TransferenciaInvalidaException montoInvalido() {
        return new TransferenciaInvalidaException(
                "El monto de la transferencia debe ser mayor a cero");
    }
}
