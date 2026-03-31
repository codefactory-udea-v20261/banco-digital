package com.udea.bancodigital.customers.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * HU3 — DTO de entrada para actualización parcial de cliente (PATCH).
 *
 * REGLAS:
 * - Todos los campos son opcionales (actualización parcial)
 * - numeroCedula NO se incluye (campo inmutable)
 * - Validaciones aplican SOLO si el campo viene en el request
 *
 * Responsable: Carlos
 */
@Getter
@Builder
public class ActualizarClienteRequestDto {

    @Size(max = 100, message = "El primer nombre no puede exceder 100 caracteres")
    private String primerNombre;

    @Size(max = 100, message = "El segundo nombre no puede exceder 100 caracteres")
    private String segundoNombre;

    @Size(max = 100, message = "El primer apellido no puede exceder 100 caracteres")
    private String primerApellido;

    @Size(max = 100, message = "El segundo apellido no puede exceder 100 caracteres")
    private String segundoApellido;

    @Email(message = "El email no tiene un formato válido")
    @Size(max = 255, message = "El email no puede exceder 255 caracteres")
    private String email;

    @Pattern(
            regexp = "^$|^[0-9]{7,20}$",
            message = "El teléfono debe contener solo números y tener entre 7 y 20 dígitos"
    )
    private String telefono;

    // No lleva @Past ni @NotNull porque es parcial
    //private LocalDate fechaNacimiento;

    // Campo opcional permitir activar/desactivar
    private Boolean activo;


}

