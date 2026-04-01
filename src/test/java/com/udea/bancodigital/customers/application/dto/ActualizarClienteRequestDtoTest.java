package com.udea.bancodigital.customers.application.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ActualizarClienteRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void deberiaFallar_siEmailEsInvalido() {
        ActualizarClienteRequestDto dto = ActualizarClienteRequestDto.builder()
                .email("correo-invalido")
                .build();

        Set<ConstraintViolation<ActualizarClienteRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void deberiaFallar_siTelefonoTieneLetras() {
        ActualizarClienteRequestDto dto = ActualizarClienteRequestDto.builder()
                .telefono("abc123")
                .build();

        Set<ConstraintViolation<ActualizarClienteRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void deberiaPasar_siDatosSonValidos() {
        ActualizarClienteRequestDto dto = ActualizarClienteRequestDto.builder()
                .primerNombre("Carlos")
                .email("carlos@test.com")
                .telefono("3001234567")
                .build();

        Set<ConstraintViolation<ActualizarClienteRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void noDebeExistirCampoCedula() {
        // Valida la regla arquitectónica de inmutabilidad en el DTO
        assertThrows(NoSuchFieldException.class, () ->
                ActualizarClienteRequestDto.class.getDeclaredField("numeroCedula")
        );
    }

    @Test
    void deberiaFallar_siNombreExcede100Caracteres() {
        // Simula un nombre de 101 caracteres para probar el @Size(max = 100)
        String nombreLargo = "A".repeat(101);
        ActualizarClienteRequestDto dto = ActualizarClienteRequestDto.builder()
                .primerNombre(nombreLargo)
                .build();

        Set<ConstraintViolation<ActualizarClienteRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("El primer nombre no puede exceder 100 caracteres",
                violations.iterator().next().getMessage());
    }

    @Test
    void deberiaFallar_siTelefonoEsMuyCorto() {
        // Tu regex [0-9]{7,20} exige mínimo 7 dígitos si no es vacío
        ActualizarClienteRequestDto dto = ActualizarClienteRequestDto.builder()
                .telefono("123456")
                .build();

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void deberiaFallar_siTelefonoEsMuyLargo() {
        // Excede los 20 dígitos permitidos por el regex
        ActualizarClienteRequestDto dto = ActualizarClienteRequestDto.builder()
                .telefono("123456789012345678901")
                .build();

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void deberiaPasar_siTelefonoEsVacio() {
        // Valida que el regex ^$ permita strings vacíos (típico en PATCH parcial)
        ActualizarClienteRequestDto dto = ActualizarClienteRequestDto.builder()
                .telefono("")
                .build();

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void deberiaPasar_siTodoEsNulo() {
        // Valida que el DTO sea verdaderamente opcional (JSON {})
        ActualizarClienteRequestDto dto = ActualizarClienteRequestDto.builder().build();

        assertTrue(validator.validate(dto).isEmpty());
    }

}