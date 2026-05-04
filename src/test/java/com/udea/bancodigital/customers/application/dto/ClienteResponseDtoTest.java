package com.udea.bancodigital.customers.application.dto;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class ClienteResponseDtoTest {

    @Test
    void testBuilderAndGetters() {
        UUID id = UUID.randomUUID();
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        Instant now = Instant.now();

        ClienteResponseDto dto = ClienteResponseDto.builder()
                .id(id)
                .numeroCedula("123456789")
                .primerNombre("Juan")
                .segundoNombre("Pablo")
                .primerApellido("Perez")
                .segundoApellido("Gomez")
                .email("juan@test.com")
                .telefono("+573001234567")
                .fechaNacimiento(birthDate)
                .activo(true)
                .createdAt(now)
                .build();

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getNumeroCedula()).isEqualTo("123456789");
        assertThat(dto.getPrimerNombre()).isEqualTo("Juan");
        assertThat(dto.getSegundoNombre()).isEqualTo("Pablo");
        assertThat(dto.getPrimerApellido()).isEqualTo("Perez");
        assertThat(dto.getSegundoApellido()).isEqualTo("Gomez");
        assertThat(dto.getEmail()).isEqualTo("juan@test.com");
        assertThat(dto.getTelefono()).isEqualTo("+573001234567");
        assertThat(dto.getFechaNacimiento()).isEqualTo(birthDate);
        assertThat(dto.isActivo()).isTrue();
        assertThat(dto.getCreatedAt()).isEqualTo(now);
    }
}
