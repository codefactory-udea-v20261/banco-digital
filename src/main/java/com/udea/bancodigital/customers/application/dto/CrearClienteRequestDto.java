package com.udea.bancodigital.customers.application.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * DTO de entrada para registro de cliente.
 * Las validaciones (@NotBlank, @Email, etc.) actúan como primera línea de defensa.
 * El GlobalExceptionHandler convierte los errores en ApiResponse estándar.
 */
@Getter
@Builder
public class CrearClienteRequestDto {

        @NotBlank(message = "El número de cédula es obligatorio")
        @Size(min = 7, max = 20, message = "La cédula debe tener entre 7 y 20 caracteres")
        private String numeroCedula;

        @NotBlank(message = "El primer nombre es obligatorio")
        @Size(max = 100, message = "El primer nombre no puede exceder 100 caracteres")
        @Pattern(
                regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$",
                message = "El primer nombre solo debe contener letras"
        )
        private String primerNombre;

        @Size(max = 100, message = "El segundo nombre no puede exceder 100 caracteres")
        @Pattern(
                regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]*$",
                message = "El segundo nombre solo debe contener letras"
        )
        private String segundoNombre;

        @NotBlank(message = "El primer apellido es obligatorio")
        @Size(max = 100, message = "El primer apellido no puede exceder 100 caracteres")
        @Pattern(
                regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$",
                message = "El primer apellido solo debe contener letras"
        )
        private String primerApellido;

        @Size(max = 100, message = "El segundo apellido no puede exceder 100 caracteres")
        @Pattern(
                regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]*$",
                message = "El segundo apellido solo debe contener letras"
        )
        private String segundoApellido;

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 255, message = "El email no puede exceder 255 caracteres")
        private String email;

        @Pattern(
                regexp = "^[0-9]{7,20}$",
                message = "El teléfono debe contener solo números y tener entre 7 y 20 dígitos"
        )
        private String telefono;

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser en el pasado")
        private LocalDate fechaNacimiento;

}
