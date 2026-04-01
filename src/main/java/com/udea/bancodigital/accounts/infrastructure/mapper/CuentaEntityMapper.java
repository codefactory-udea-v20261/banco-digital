package com.udea.bancodigital.accounts.infrastructure.mapper;

import com.udea.bancodigital.accounts.domain.model.Cuenta;
import com.udea.bancodigital.accounts.domain.model.EstadoCuenta;
import com.udea.bancodigital.accounts.domain.model.TipoCuenta;
import com.udea.bancodigital.accounts.infrastructure.entity.CuentaEntity;
import org.springframework.stereotype.Component;

@Component
public class CuentaEntityMapper {

    public Cuenta toDomain(CuentaEntity entity) {
        return Cuenta.builder()
                .id(entity.getId())
                .numeroCuenta(entity.getNumeroCuenta())
                .clienteId(entity.getClienteId())
                .tipoCuenta(TipoCuenta.fromId(entity.getTipoCuentaId()))
                .saldo(entity.getSaldo())
                .estado(EstadoCuenta.valueOf(entity.getEstado()))
                .fechaApertura(entity.getFechaApertura())
                .build();
    }

    public CuentaEntity toEntity(Cuenta cuenta) {
        return CuentaEntity.builder()
                .id(cuenta.getId())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .clienteId(cuenta.getClienteId())
                .tipoCuentaId(cuenta.getTipoCuenta().getId())
                .saldo(cuenta.getSaldo())
                .estado(cuenta.getEstado().name())
                .fechaApertura(cuenta.getFechaApertura())
                .build();
    }
}
