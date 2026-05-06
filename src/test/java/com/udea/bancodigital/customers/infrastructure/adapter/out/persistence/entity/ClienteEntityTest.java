package com.udea.bancodigital.customers.infrastructure.adapter.out.persistence.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ClienteEntity Tests")
class ClienteEntityTest {

    @Test
    @DisplayName("Should create ClienteEntity with builder")
    void shouldCreateEntityWithBuilder() {
        UUID id = UUID.randomUUID();
        LocalDate fechaNacimiento = LocalDate.of(1990, 1, 1);
        
        ClienteEntity entity = ClienteEntity.builder()
                .id(id)
                .numeroCedula("123456789")
                .primerNombre("Juan")
                .segundoNombre("Carlos")
                .primerApellido("Perez")
                .segundoApellido("Garcia")
                .email("juan@example.com")
                .telefono("3012345678")
                .fechaNacimiento(fechaNacimiento)
                .activo(true)
                .build();

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getNumeroCedula()).isEqualTo("123456789");
        assertThat(entity.getPrimerNombre()).isEqualTo("Juan");
        assertThat(entity.getSegundoNombre()).isEqualTo("Carlos");
        assertThat(entity.getPrimerApellido()).isEqualTo("Perez");
        assertThat(entity.getSegundoApellido()).isEqualTo("Garcia");
        assertThat(entity.getEmail()).isEqualTo("juan@example.com");
        assertThat(entity.getTelefono()).isEqualTo("3012345678");
        assertThat(entity.getFechaNacimiento()).isEqualTo(fechaNacimiento);
        assertThat(entity.isActivo()).isTrue();
    }

    @Test
    @DisplayName("Should create ClienteEntity with no-arg constructor")
    void shouldCreateEntityWithNoArgConstructor() {
        ClienteEntity entity = new ClienteEntity();

        assertThat(entity.getId()).isNull();
        assertThat(entity.getNumeroCedula()).isNull();
        assertThat(entity.isActivo()).isTrue();
    }

    @Test
    @DisplayName("Should create ClienteEntity with all-arg constructor")
    void shouldCreateEntityWithAllArgConstructor() {
        UUID id = UUID.randomUUID();
        LocalDate fechaNacimiento = LocalDate.of(1990, 1, 1);

        ClienteEntity entity = new ClienteEntity(
                id,
                "123456789",
                "Juan",
                "Carlos",
                "Perez",
                "Garcia",
                "juan@example.com",
                "3012345678",
                fechaNacimiento,
                true
        );

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getNumeroCedula()).isEqualTo("123456789");
        assertThat(entity.getPrimerNombre()).isEqualTo("Juan");
        assertThat(entity.isActivo()).isTrue();
    }

    @Test
    @DisplayName("Should set and get properties")
    void shouldSetAndGetProperties() {
        ClienteEntity entity = new ClienteEntity();
        UUID id = UUID.randomUUID();
        LocalDate fechaNacimiento = LocalDate.of(1985, 5, 15);

        entity.setId(id);
        entity.setNumeroCedula("987654321");
        entity.setPrimerNombre("Maria");
        entity.setSegundoNombre("Elena");
        entity.setPrimerApellido("Lopez");
        entity.setSegundoApellido("Martinez");
        entity.setEmail("maria@example.com");
        entity.setTelefono("3019876543");
        entity.setFechaNacimiento(fechaNacimiento);
        entity.setActivo(false);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getNumeroCedula()).isEqualTo("987654321");
        assertThat(entity.getPrimerNombre()).isEqualTo("Maria");
        assertThat(entity.getSegundoNombre()).isEqualTo("Elena");
        assertThat(entity.getPrimerApellido()).isEqualTo("Lopez");
        assertThat(entity.getSegundoApellido()).isEqualTo("Martinez");
        assertThat(entity.getEmail()).isEqualTo("maria@example.com");
        assertThat(entity.getTelefono()).isEqualTo("3019876543");
        assertThat(entity.getFechaNacimiento()).isEqualTo(fechaNacimiento);
        assertThat(entity.isActivo()).isFalse();
    }

    @Test
    @DisplayName("Should create active entity by default with builder")
    void shouldCreateActiveEntityByDefault() {
        ClienteEntity entity = ClienteEntity.builder()
                .numeroCedula("123456789")
                .primerNombre("Juan")
                .primerApellido("Perez")
                .email("juan@example.com")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .build();

        assertThat(entity.isActivo()).isTrue();
    }

    @Test
    @DisplayName("Should allow null for optional fields")
    void shouldAllowNullForOptionalFields() {
        ClienteEntity entity = ClienteEntity.builder()
                .numeroCedula("123456789")
                .primerNombre("Juan")
                .primerApellido("Perez")
                .email("juan@example.com")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .segundoNombre(null)
                .segundoApellido(null)
                .telefono(null)
                .build();

        assertThat(entity.getSegundoNombre()).isNull();
        assertThat(entity.getSegundoApellido()).isNull();
        assertThat(entity.getTelefono()).isNull();
    }
}
