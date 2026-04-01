package com.udea.bancodigital.accounts.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.With;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entidad de DOMINIO - Cuenta.
 * Se aplica inmutabilidad para proteger el estado financiero.
 */
@Getter
@Builder(toBuilder = true) // toBuilder permite clonar desde una instancia
@With
public class Cuenta {

    // Campos de identidad (Inmutables una vez creada la cuenta)
    private final UUID id;
    private final String numeroCuenta;
    private final UUID clienteId;
    private final String tipoCuenta;

    // Campos de estado (Cambiantes mediante evolución de estado, no mutación)
    private final BigDecimal saldo; // Cambiado a final para forzar inmutabilidad
    private final boolean activa;   // Cambiado a final para forzar inmutabilidad
}