package com.udea.bancodigital.accounts.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class CuentaResponseDto {
    private UUID id;
    private String numeroCuenta;
    private UUID clienteId;
    private String tipoCuenta;
    private BigDecimal saldo;
    private String estado;
    private LocalDate fechaApertura;
}
