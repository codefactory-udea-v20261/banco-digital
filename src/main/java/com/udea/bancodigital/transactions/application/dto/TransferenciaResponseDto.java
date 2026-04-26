package com.udea.bancodigital.transactions.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaResponseDto {

    private UUID transaccionId;

    private UUID cuentaOrigenId;

    private UUID cuentaDestinoId;

    private BigDecimal monto;

    private String referencia;

    private String estado;

}