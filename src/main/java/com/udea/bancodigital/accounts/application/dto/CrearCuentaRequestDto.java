package com.udea.bancodigital.accounts.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearCuentaRequestDto {
    @NotNull(message = "El ID del cliente es obligatorio")
    private UUID clienteId;

    @NotNull(message = "El tipo de cuenta es obligatorio")
    private String tipoCuenta;
}
