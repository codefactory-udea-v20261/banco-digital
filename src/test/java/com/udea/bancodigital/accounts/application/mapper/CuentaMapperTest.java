package com.udea.bancodigital.accounts.application.mapper;

import com.udea.bancodigital.accounts.application.dto.CuentaResponseDto;
import com.udea.bancodigital.accounts.domain.model.Cuenta;
import com.udea.bancodigital.accounts.domain.model.EstadoCuenta;
import com.udea.bancodigital.accounts.domain.model.TipoCuenta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CuentaMapperTest {

    private final CuentaMapper mapper = new CuentaMapper();

    @Test
    void toResponseDto_Success() {
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        TipoCuenta tipo = TipoCuenta.AHORRO;
        
        Cuenta cuenta = Cuenta.builder()
                .id(id)
                .numeroCuenta("ACC123")
                .clienteId(clienteId)
                .tipoCuenta(tipo)
                .saldo(new BigDecimal("100.00"))
                .estado(EstadoCuenta.ACTIVA)
                .fechaApertura(LocalDate.now())
                .build();

        CuentaResponseDto dto = mapper.toResponseDto(cuenta);

        assertEquals(id, dto.getId());
        assertEquals("ACC123", dto.getNumeroCuenta());
        assertEquals("AHORRO", dto.getTipoCuenta());
        assertEquals("ACTIVA", dto.getEstado());
    }
}
