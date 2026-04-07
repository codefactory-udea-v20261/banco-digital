package com.udea.bancodigital.accounts.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ConsultarSaldoTotalResponseDto {
    private BigDecimal saldoTotal;
}
