package com.udea.bancodigital.transactions.domain.port.out;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface CuentaServicePort {
    // Para validar existencia y saldo
    Optional<BigDecimal> consultarSaldo(UUID cuentaId);

    // Para el descuento del retiro (OLTP)
    void actualizarSaldo(UUID cuentaId, BigDecimal nuevoSaldo);

    boolean existeCuenta(UUID cuentaId);
}
