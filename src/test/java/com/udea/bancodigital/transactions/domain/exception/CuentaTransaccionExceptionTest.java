package com.udea.bancodigital.transactions.domain.exception;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class CuentaTransaccionExceptionTest {

    @Test
    void testConstructorAndNoEncontrada() {
        CuentaTransaccionException ex1 = new CuentaTransaccionException("12345");
        assertThat(ex1.getErrorCode()).isEqualTo("CUENTA_INVALIDA");
        assertThat(ex1.getMessage()).contains("La cuenta 12345 es invalida");
        
        UUID id = UUID.randomUUID();
        CuentaTransaccionException ex2 = CuentaTransaccionException.noEncontrada(id);
        assertThat(ex2.getErrorCode()).isEqualTo("CUENTA_INVALIDA");
        assertThat(ex2.getMessage()).contains(id.toString());
    }
}
