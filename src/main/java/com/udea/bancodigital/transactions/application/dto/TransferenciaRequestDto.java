package com.udea.bancodigital.transactions.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaRequestDto {

    private UUID cuentaOrigenId;

    private UUID cuentaDestinoId;

    private BigDecimal monto;

}