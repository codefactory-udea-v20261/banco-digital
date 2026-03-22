package com.udea.bancodigital.transactions.domain.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * HU7 / HU8 — Se lanza cuando el saldo disponible no cubre el monto de la operación.
 * La lógica transaccional que la genera vive en la capa application (@Transactional) — ADR-001.
 */
public class SaldoInsuficienteException extends BusinessException {

    public SaldoInsuficienteException(UUID cuentaId, BigDecimal saldoActual, BigDecimal montoSolicitado) {
        super("SALDO_INSUFICIENTE",
              String.format("Saldo insuficiente en cuenta %s. Disponible: %.2f, Solicitado: %.2f",
                      cuentaId, saldoActual, montoSolicitado),
              HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
