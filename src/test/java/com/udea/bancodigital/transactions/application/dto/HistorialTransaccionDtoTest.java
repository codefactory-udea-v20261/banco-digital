package com.udea.bancodigital.transactions.application.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class HistorialTransaccionDtoTest {

    @Test
    void testBuilderAndGetters() {
        OffsetDateTime now = OffsetDateTime.now();
        HistorialTransaccionDto dto = HistorialTransaccionDto.builder()
                .fechaHora(now)
                .tipo("RETIRO")
                .monto(new BigDecimal("100.50"))
                .build();

        assertThat(dto.getFechaHora()).isEqualTo(now);
        assertThat(dto.getTipo()).isEqualTo("RETIRO");
        assertThat(dto.getMonto()).isEqualTo(new BigDecimal("100.50"));
    }
}
