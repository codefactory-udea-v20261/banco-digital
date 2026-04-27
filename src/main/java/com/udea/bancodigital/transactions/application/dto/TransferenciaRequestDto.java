package com.udea.bancodigital.transactions.application.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaRequestDto {

    private String numeroCuentaOrigen;

    private String numeroCuentaDestino;

    private BigDecimal monto;

}