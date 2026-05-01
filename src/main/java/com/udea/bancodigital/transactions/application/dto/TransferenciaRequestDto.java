package com.udea.bancodigital.transactions.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaRequestDto {

    @NotBlank
    private String numeroCuentaOrigen;

    @NotBlank
    private String numeroCuentaDestino;

    @NotNull
    @DecimalMin(value = "0.01",
            message = "El monto debe ser mayor a cero")
    private BigDecimal monto;

}