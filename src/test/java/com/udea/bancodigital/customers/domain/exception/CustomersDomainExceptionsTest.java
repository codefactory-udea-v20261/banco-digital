package com.udea.bancodigital.customers.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Excepciones de dominio - Customers")
class CustomersDomainExceptionsTest {
    @Test
    @DisplayName("CampoCedularInmutableException debe tener código CAMPO_INMUTABLE y status BAD_REQUEST")
    void campoCedularInmutableException() {
        CampoCedularInmutableException ex = new CampoCedularInmutableException();

        assertThat(ex.getErrorCode()).isEqualTo("CAMPO_INMUTABLE");
        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex.getMessage()).containsIgnoringCase("cédula");
    }

    @Test
    @DisplayName("CampoInmutableException debe incluir el nombre del campo en el mensaje")
    void campoInmutableException() {
        CampoInmutableException ex = new CampoInmutableException("fechaNacimiento");

        assertThat(ex.getMessage()).contains("fechaNacimiento");
    }

    @Test
    @DisplayName("ClienteNoAutorizadoException debe tener status FORBIDDEN e incluir el ID")
    void clienteNoAutorizadoException() {
        UUID id = UUID.randomUUID();
        ClienteNoAutorizadoException ex = new ClienteNoAutorizadoException(id);

        assertThat(ex.getErrorCode()).isEqualTo("CLIENTE_NO_AUTORIZADO");
        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(ex.getMessage()).contains(id.toString());
    }

    @Test
    @DisplayName("ClienteYaExisteException por email debe tener status CONFLICT")
    void clienteYaExisteExceptionPorEmail() {
        ClienteYaExisteException ex = new ClienteYaExisteException("juan@banco.com");

        assertThat(ex.getErrorCode()).isEqualTo("CLIENTE_ALREADY_EXISTS");
        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getMessage()).contains("juan@banco.com");
    }

    @Test
    @DisplayName("ClienteYaExisteException por campo/valor debe incluir ambos en el mensaje")
    void clienteYaExisteExceptionPorCampoValor() {
        ClienteYaExisteException ex = new ClienteYaExisteException("cédula", "12345678");

        assertThat(ex.getErrorCode()).isEqualTo("CLIENTE_ALREADY_EXISTS");
        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getMessage()).contains("cédula").contains("12345678");
    }

}
