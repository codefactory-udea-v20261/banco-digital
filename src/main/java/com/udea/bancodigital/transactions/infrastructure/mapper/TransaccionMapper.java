package com.udea.bancodigital.transactions.infrastructure.mapper;

import com.udea.bancodigital.transactions.domain.model.Transaccion;
import com.udea.bancodigital.transactions.infrastructure.entity.TransaccionEntity;

import org.springframework.stereotype.Component;

@Component
public class TransaccionMapper {

    public TransaccionEntity toEntity(
            Transaccion domain) {

        if (domain == null) {
            return null;
        }

        return TransaccionEntity.builder()
                .id(domain.getId())
                .cuentaOrigenId(domain.getCuentaOrigenId())
                .cuentaDestinoId(domain.getCuentaDestinoId())
                .tipoId(domain.getTipoId())
                .monto(domain.getMonto())
                .saldoAnterior(domain.getSaldoAnterior())
                .saldoPosterior(domain.getSaldoPosterior())
                .descripcion(domain.getDescripcion())
                .referencia(domain.getReferencia())
                .estado(domain.getEstado())
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .build();
    }

    public Transaccion toDomain(
            TransaccionEntity entity) {

        if (entity == null) {
            return null;
        }

        return Transaccion.builder()
                .id(entity.getId())
                .cuentaOrigenId(entity.getCuentaOrigenId())
                .cuentaDestinoId(entity.getCuentaDestinoId())
                .tipoId(entity.getTipoId())
                .monto(entity.getMonto())
                .saldoAnterior(entity.getSaldoAnterior())
                .saldoPosterior(entity.getSaldoPosterior())
                .descripcion(entity.getDescripcion())
                .referencia(entity.getReferencia())
                .estado(entity.getEstado())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }

}