package com.udea.bancodigital.transactions.application.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class RetiroRequestDtoTest {

    @Test
    void testGettersAndConstructors() {
        UUID cuentaId = UUID.randomUUID();
        BigDecimal monto = new BigDecimal("100.0");
        String desc = "Retiro en ATM";

        RetiroRequestDto dto1 = new RetiroRequestDto();
        assertThat(dto1).isNotNull();

        RetiroRequestDto dto2 = new RetiroRequestDto(cuentaId, monto, desc);
        assertThat(dto2.getCuentaId()).isEqualTo(cuentaId);
        assertThat(dto2.getMonto()).isEqualTo(monto);
        assertThat(dto2.getDescripcion()).isEqualTo(desc);

        RetiroRequestDto dto3 = RetiroRequestDto.builder()
                .cuentaId(cuentaId)
                .monto(monto)
                .descripcion(desc)
                .build();
        assertThat(dto3.getCuentaId()).isEqualTo(cuentaId);
    }
}
