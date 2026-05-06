package com.udea.bancodigital.accounts.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Excepciones de dominio - Accounts")
class AccountsDomainExceptionsTest {
    @Test
    @DisplayName("CuentaInactivaException debe incluir el ID y tener status UNPROCESSABLE_ENTITY")
    void cuentaInactivaException() {
        UUID id = UUID.randomUUID();
        CuentaInactivaException ex = new CuentaInactivaException(id);

        assertThat(ex.getMessage()).contains(id.toString());
        assertThat(ex.getErrorCode()).isEqualTo("CUENTA_INACTIVA");
        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    @DisplayName("CuentaNoEncontradaException por UUID debe tener status NOT_FOUND")
    void cuentaNoEncontradaExceptionPorUuid() {
        UUID id = UUID.randomUUID();
        CuentaNoEncontradaException ex = new CuentaNoEncontradaException(id);

        assertThat(ex.getErrorCode()).isEqualTo("CUENTA_NO_ENCONTRADA");
        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getMessage()).contains(id.toString());
    }

    @Test
    @DisplayName("CuentaNoEncontradaException por numeroCuenta debe tener status NOT_FOUND")
    void cuentaNoEncontradaExceptionPorNumeroCuenta() {
        CuentaNoEncontradaException ex = new CuentaNoEncontradaException("CO4051234567890");

        assertThat(ex.getErrorCode()).isEqualTo("CUENTA_NO_ENCONTRADA");
        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getMessage()).contains("CO4051234567890");
    }

    @Test
    @DisplayName("CuentaNoPerteneceAlClienteException debe tener status FORBIDDEN")
    void cuentaNoPerteneceAlClienteException() {
        UUID cuentaId = UUID.randomUUID();
        CuentaNoPerteneceAlClienteException ex = new CuentaNoPerteneceAlClienteException(cuentaId);

        assertThat(ex.getErrorCode()).isEqualTo("CUENTA_NO_AUTORIZADA");
        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(ex.getMessage()).contains(cuentaId.toString());
    }

    @Test
    @DisplayName("ClienteInactivoException debe incluir el clienteId y tener status UNPROCESSABLE_ENTITY")
    void clienteInactivoException() {
        UUID clienteId = UUID.randomUUID();
        ClienteInactivoException ex = new ClienteInactivoException(clienteId);

        assertThat(ex.getErrorCode()).isEqualTo("CLIENTE_INACTIVO");
        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(ex.getMessage()).contains(clienteId.toString());
    }

    @Test
    @DisplayName("TipoCuentaInvalidoException debe incluir el tipo inválido y tener status BAD_REQUEST")
    void tipoCuentaInvalidoException() {
        TipoCuentaInvalidoException ex = new TipoCuentaInvalidoException("PLAZO_FIJO");

        assertThat(ex.getErrorCode()).isEqualTo("TIPO_CUENTA_INVALIDO");
        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex.getMessage()).contains("PLAZO_FIJO");
    }

}
