package com.udea.bancodigital.customers.application.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class CrearClienteRequestDtoTest {

    @Test
    void testCrearClienteRequestDto() {
        LocalDate birth = LocalDate.of(1990, 1, 1);
        CrearClienteRequestDto dto = CrearClienteRequestDto.builder()
                .numeroCedula("123")
                .primerNombre("Juan")
                .primerApellido("Perez")
                .email("juan@example.com")
                .fechaNacimiento(birth)
                .build();
        
        assertEquals("123", dto.getNumeroCedula());
        assertEquals("Juan", dto.getPrimerNombre());
        assertEquals("juan@example.com", dto.getEmail());
        assertEquals(birth, dto.getFechaNacimiento());
    }
}
