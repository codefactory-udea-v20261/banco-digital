package com.udea.bancodigital.accounts.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
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
    private final TipoCuenta tipoCuenta;

    // Campos de estado (Cambiantes mediante evolución de estado, no mutación)
    private final BigDecimal saldo;
    private final EstadoCuenta estado;
    private final LocalDate fechaApertura;

    public boolean isActiva() {
        return EstadoCuenta.ACTIVA == estado;
    }

    public static Cuenta crearNueva(UUID clienteId, TipoCuenta tipoCuenta, String numeroCuenta) {
        Objects.requireNonNull(clienteId, "El clienteId es obligatorio");
        Objects.requireNonNull(tipoCuenta, "El tipoCuenta es obligatorio");
        if (numeroCuenta == null || numeroCuenta.isBlank()) {
            throw new IllegalArgumentException("El numeroCuenta es obligatorio");
        }

        return Cuenta.builder()
                .id(UUID.randomUUID())
                .numeroCuenta(numeroCuenta)
                .clienteId(clienteId)
                .tipoCuenta(tipoCuenta)
                // TODO: Reemplazar este saldo inicial temporal con un endpoint para recargar saldo.
                .saldo(new BigDecimal("100000"))
                .estado(EstadoCuenta.ACTIVA)
                .fechaApertura(LocalDate.now())
                .build();
    }
}
