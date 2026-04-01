package com.udea.bancodigital.accounts.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "El tipo de cuenta es obligatorio")
    @Pattern(
            regexp = "(?i)AHORRO(S)?|CORRIENTE",
            message = "El tipo de cuenta debe ser AHORRO o CORRIENTE"
    )
    private String tipoCuenta;
}
