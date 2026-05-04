package com.udea.bancodigital.transactions.application.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class TransferenciaResponseDtoTest {

    @Test
    void testGettersSettersAndConstructors() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();
        BigDecimal monto = new BigDecimal("150.0");
        String ref = "ref123";
        String estado = "COMPLETED";

        TransferenciaResponseDto dto1 = new TransferenciaResponseDto();
        dto1.setTransaccionId(id1);
        dto1.setCuentaOrigenId(id2);
        dto1.setCuentaDestinoId(id3);
        dto1.setMonto(monto);
        dto1.setReferencia(ref);
        dto1.setEstado(estado);

        assertThat(dto1.getTransaccionId()).isEqualTo(id1);
        assertThat(dto1.getCuentaOrigenId()).isEqualTo(id2);
        assertThat(dto1.getCuentaDestinoId()).isEqualTo(id3);
        assertThat(dto1.getMonto()).isEqualTo(monto);
        assertThat(dto1.getReferencia()).isEqualTo(ref);
        assertThat(dto1.getEstado()).isEqualTo(estado);

        TransferenciaResponseDto dto2 = new TransferenciaResponseDto(id1, id2, id3, monto, ref, estado);
        assertThat(dto2.getTransaccionId()).isEqualTo(id1);

        TransferenciaResponseDto dto3 = TransferenciaResponseDto.builder()
                .transaccionId(id1)
                .cuentaOrigenId(id2)
                .cuentaDestinoId(id3)
                .monto(monto)
                .referencia(ref)
                .estado(estado)
                .build();
        assertThat(dto3.getTransaccionId()).isEqualTo(id1);
    }
}
