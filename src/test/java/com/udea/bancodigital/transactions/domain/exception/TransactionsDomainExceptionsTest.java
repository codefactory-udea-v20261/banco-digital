package com.udea.bancodigital.transactions.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Excepciones de dominio - Transactions")
class TransactionsDomainExceptionsTest {
    @Test
    @DisplayName("SaldoInsuficienteException debe incluir cuenta, saldo y monto en el mensaje")
    void saldoInsuficienteException() {
        UUID cuentaId = UUID.randomUUID();
        SaldoInsuficienteException ex = new SaldoInsuficienteException(
                cuentaId, new BigDecimal("500.00"), new BigDecimal("1000.00"));

        assertThat(ex.getErrorCode()).isEqualTo("SALDO_INSUFICIENTE");
        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(ex.getMessage()).contains(cuentaId.toString());
        assertThat(ex.getMessage()).contains("500");
        assertThat(ex.getMessage()).contains("1000");
    }

    @Test
    @DisplayName("TransferenciaInvalidaException debe tener código TRANSFERENCIA_INVALIDA y status BAD_REQUEST")
    void transferenciaInvalidaException() {
        TransferenciaInvalidaException ex = new TransferenciaInvalidaException("Motivo de prueba");

        assertThat(ex.getErrorCode()).isEqualTo("TRANSFERENCIA_INVALIDA");
        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex.getMessage()).isEqualTo("Motivo de prueba");
    }

    @Test
    @DisplayName("TransferenciaInvalidaException.mismaCuenta() debe indicar misma cuenta")
    void transferenciaInvalidaMismaCuenta() {
        TransferenciaInvalidaException ex = TransferenciaInvalidaException.mismaCuenta();

        assertThat(ex.getErrorCode()).isEqualTo("TRANSFERENCIA_INVALIDA");
        assertThat(ex.getMessage()).containsIgnoringCase("origen").containsIgnoringCase("destino");
    }

    @Test
    @DisplayName("TransferenciaInvalidaException.montoInvalido() debe indicar monto inválido")
    void transferenciaInvalidaMontoInvalido() {
        TransferenciaInvalidaException ex = TransferenciaInvalidaException.montoInvalido();

        assertThat(ex.getErrorCode()).isEqualTo("TRANSFERENCIA_INVALIDA");
        assertThat(ex.getMessage()).containsIgnoringCase("monto");
    }

}
