package com.udea.bancodigital.accounts.application.mapper;

import com.udea.bancodigital.accounts.application.dto.CuentaResponseDto;
import com.udea.bancodigital.accounts.domain.model.Cuenta;
import org.springframework.stereotype.Component;

@Component
public class CuentaMapper {

    public CuentaResponseDto toResponseDto(Cuenta cuenta) {
        return CuentaResponseDto.builder()
                .id(cuenta.getId())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .clienteId(cuenta.getClienteId())
                .tipoCuenta(cuenta.getTipoCuenta().getNombre())
                .saldo(cuenta.getSaldo())
                .estado(cuenta.getEstado().name())
                .fechaApertura(cuenta.getFechaApertura())
                .build();
    }
}
