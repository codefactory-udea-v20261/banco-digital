package com.udea.bancodigital.accounts.infrastructure.entity;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class CuentaEntityTest {

    static class TestCuentaEntity extends CuentaEntity {
        public void triggerPrePersist() {
            super.prePersist();
        }
    }

    @Test
    void testCuentaEntity_GettersSettersAndAuditing() {
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        BigDecimal saldo = new BigDecimal("1000.00");
        
        TestCuentaEntity cuenta = new TestCuentaEntity();
        cuenta.setId(id);
        cuenta.setNumeroCuenta("ACC123");
        cuenta.setClienteId(clienteId);
        cuenta.setTipoCuentaId((short)1);
        cuenta.setSaldo(saldo);
        cuenta.setEstado("ACTIVA");
        cuenta.setFechaApertura(LocalDate.now());

        assertEquals(id, cuenta.getId());
        assertEquals("ACC123", cuenta.getNumeroCuenta());
        assertEquals(clienteId, cuenta.getClienteId());
        assertEquals(saldo, cuenta.getSaldo());
        assertEquals("ACTIVA", cuenta.getEstado());

        cuenta.triggerPrePersist();
        assertNotNull(cuenta.getCreatedAt());
        assertEquals("SYSTEM", cuenta.getCreatedBy());
    }
    
    @Test
    void testNoArgsConstructor() {
        CuentaEntity cuenta = new CuentaEntity();
        assertNotNull(cuenta);
    }
}
