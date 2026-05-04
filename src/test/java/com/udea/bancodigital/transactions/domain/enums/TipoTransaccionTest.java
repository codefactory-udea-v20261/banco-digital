package com.udea.bancodigital.transactions.domain.enums;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class TipoTransaccionTest {

    @Test
    void testEnumValuesAndGetters() {
        TipoTransaccion retiro = TipoTransaccion.RETIRO;
        assertThat(retiro.getId()).isEqualTo((short) 1);
        assertThat(retiro.getDescripcion()).isEqualTo("Retiro de efectivo");
    }

    @Test
    void testFromId() {
        assertThat(TipoTransaccion.fromId((short) 1)).isEqualTo(TipoTransaccion.RETIRO);
        assertThat(TipoTransaccion.fromId((short) 2)).isEqualTo(TipoTransaccion.DEPOSITO);
        assertThat(TipoTransaccion.fromId((short) 3)).isEqualTo(TipoTransaccion.TRANSFERENCIA_ENVIADA);
        assertThat(TipoTransaccion.fromId((short) 4)).isEqualTo(TipoTransaccion.TRANSFERENCIA_RECIBIDA);
        
        assertThat(TipoTransaccion.fromId((short) 99)).isNull();
        assertThat(TipoTransaccion.fromId(null)).isNull();
    }
}
