package com.udea.bancodigital.transactions.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Builder
public class HistorialTransaccionDto {
    private OffsetDateTime fechaHora;
    private String tipo;
    private BigDecimal monto;
}
