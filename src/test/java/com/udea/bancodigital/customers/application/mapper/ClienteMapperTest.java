package com.udea.bancodigital.customers.application.mapper;

import com.udea.bancodigital.customers.application.dto.CrearClienteRequestDto;
import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.domain.model.Cliente;
import com.udea.bancodigital.customers.domain.model.Email;
import com.udea.bancodigital.customers.domain.model.NumeroCedula;
import com.udea.bancodigital.customers.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ClienteMapper")
class ClienteMapperTest {

    private final ClienteMapper mapper = Mappers.getMapper(ClienteMapper.class);

    @Nested
    @DisplayName("toDomain(CrearClienteRequestDto)")
    class ToDomainFromDtoTest {

        @Test
        @DisplayName("Debe convertir CrearClienteRequestDto a Cliente correctamente")
        void debeConvertirCrearDtoADomain() {
            CrearClienteRequestDto dto = CrearClienteRequestDto.builder()
                    .numeroCedula("1234567890")
                    .primerNombre("Juan")
                    .segundoNombre("Carlos")
                    .primerApellido("Pérez")
                    .segundoApellido("López")
                    .email("juan.perez@example.com")
                    .telefono("3215551234")
                    .fechaNacimiento(LocalDate.of(1990, 5, 15))
                    .build();

            Cliente result = mapper.toDomain(dto);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isNull();
            assertThat(result.isActivo()).isTrue();
            assertThat(result.getNumeroCedula()).isNotNull()
                    .extracting(NumeroCedula::valor)
                    .isEqualTo("1234567890");
        }

        @Test
        @DisplayName("Debe establecer activo en true automáticamente")
        void debeEstablecerActivoEnTrue() {
            CrearClienteRequestDto dto = CrearClienteRequestDto.builder()
                    .numeroCedula("9876543210")
                    .primerNombre("María")
                    .primerApellido("García")
                    .email("maria.garcia@example.com")
                    .fechaNacimiento(LocalDate.of(1985, 10, 20))
                    .build();

            Cliente result = mapper.toDomain(dto);

            assertThat(result.isActivo()).isTrue();
        }
    }

    @Nested
    @DisplayName("toEntity(Cliente)")
    class ToEntityTest {

        @Test
        @DisplayName("Debe convertir Cliente a ClienteEntity correctamente")
        void debeConvertirDomainAEntity() {
            UUID id = UUID.randomUUID();
            LocalDate fechaNacimiento = LocalDate.of(1992, 7, 12);

            Cliente domain = Cliente.builder()
                    .id(id)
                    .numeroCedula(NumeroCedula.of("2222222222"))
                    .primerNombre("Antonio")
                    .segundoNombre("José")
                    .primerApellido("Rodríguez")
                    .segundoApellido("Sánchez")
                    .email(Email.of("antonio.rodriguez@example.com"))
                    .telefono("3001234567")
                    .fechaNacimiento(fechaNacimiento)
                    .activo(true)
                    .build();

            ClienteEntity result = mapper.toEntity(domain);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(id);
            assertThat(result.getNumeroCedula()).isEqualTo("2222222222");
            assertThat(result.getEmail()).isEqualTo("antonio.rodriguez@example.com");
        }
    }

    @Nested
    @DisplayName("toDomain(ClienteEntity)")
    class ToDomainFromEntityTest {

        @Test
        @DisplayName("Debe convertir ClienteEntity a Cliente correctamente")
        void debeConvertirEntityADomain() {
            UUID id = UUID.randomUUID();
            LocalDate fechaNacimiento = LocalDate.of(1991, 8, 22);

            ClienteEntity entity = ClienteEntity.builder()
                    .id(id)
                    .numeroCedula("5555555555")
                    .primerNombre("Elena")
                    .primerApellido("Vargas")
                    .email("elena.vargas@example.com")
                    .fechaNacimiento(fechaNacimiento)
                    .activo(true)
                    .build();

            Cliente result = mapper.toDomain(entity);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(id);
            assertThat(result.getNumeroCedula()).isNotNull()
                    .extracting(NumeroCedula::valor)
                    .isEqualTo("5555555555");
        }
    }

    @Nested
    @DisplayName("toResponseDto(Cliente)")
    class ToResponseDtoTest {

        @Test
        @DisplayName("Debe convertir Cliente a ClienteResponseDto correctamente")
        void debeConvertirClienteAResponseDto() {
            UUID id = UUID.randomUUID();
            LocalDate fechaNacimiento = LocalDate.of(1989, 12, 3);

            Cliente cliente = Cliente.builder()
                    .id(id)
                    .numeroCedula(NumeroCedula.of("8888888888"))
                    .primerNombre("Miguel")
                    .primerApellido("Torres")
                    .email(Email.of("miguel.torres@example.com"))
                    .telefono("3162567890")
                    .fechaNacimiento(fechaNacimiento)
                    .activo(true)
                    .build();

            ClienteResponseDto result = mapper.toResponseDto(cliente);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(id);
            assertThat(result.getNumeroCedula()).isEqualTo("8888888888");
            assertThat(result.getEmail()).isEqualTo("miguel.torres@example.com");
        }
    }

    @Nested
    @DisplayName("Value Object conversions")
    class ValueObjectConversionsTest {

        @Test
        @DisplayName("stringToNumeroCedula debe crear NumeroCedula válida")
        void stringToNumeroCedulaCreaNumeroCedulaValida() {
            NumeroCedula result = mapper.stringToNumeroCedula("5151515151");

            assertThat(result).isNotNull()
                    .extracting(NumeroCedula::valor)
                    .isEqualTo("5151515151");
        }

        @Test
        @DisplayName("stringToNumeroCedula debe retornar null para string null")
        void stringToNumeroCedulaRetornaNullParaStringNull() {
            NumeroCedula result = mapper.stringToNumeroCedula(null);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("stringToEmail debe crear Email válido")
        void stringToEmailCreaEmailValido() {
            Email result = mapper.stringToEmail("test@example.com");

            assertThat(result).isNotNull()
                    .extracting(Email::valor)
                    .isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("stringToEmail debe retornar null para string null")
        void stringToEmailRetornaNullParaStringNull() {
            Email result = mapper.stringToEmail(null);

            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Error handling")
    class ErrorHandlingTest {

        @Test
        @DisplayName("stringToNumeroCedula debe lanzar excepción para cédula inválida")
        void stringToNumeroCedulaLanzaExcepcionParaCedulaInvalida() {
            assertThatThrownBy(() -> mapper.stringToNumeroCedula("invalid"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("stringToEmail debe lanzar excepción para email inválido")
        void stringToEmailLanzaExcepcionParaEmailInvalido() {
            assertThatThrownBy(() -> mapper.stringToEmail("invalid-email"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
