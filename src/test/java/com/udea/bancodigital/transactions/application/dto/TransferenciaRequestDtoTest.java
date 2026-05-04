package com.udea.bancodigital.transactions.application.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

class TransferenciaRequestDtoTest {

    @Test
    void testGettersSettersAndConstructors() {
        TransferenciaRequestDto dto1 = new TransferenciaRequestDto();
        dto1.setNumeroCuentaOrigen("123");
        dto1.setNumeroCuentaDestino("456");
        dto1.setMonto(new BigDecimal("50.0"));

        assertThat(dto1.getNumeroCuentaOrigen()).isEqualTo("123");
        assertThat(dto1.getNumeroCuentaDestino()).isEqualTo("456");
        assertThat(dto1.getMonto()).isEqualTo(new BigDecimal("50.0"));

        TransferenciaRequestDto dto2 = new TransferenciaRequestDto("123", "456", new BigDecimal("50.0"));
        assertThat(dto2.getNumeroCuentaOrigen()).isEqualTo("123");

        TransferenciaRequestDto dto3 = TransferenciaRequestDto.builder()
                .numeroCuentaOrigen("123")
                .numeroCuentaDestino("456")
                .monto(new BigDecimal("50.0"))
                .build();
        assertThat(dto3.getNumeroCuentaOrigen()).isEqualTo("123");
    }
}
