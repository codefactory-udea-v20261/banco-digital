package com.udea.bancodigital.transactions.application.mapper;

import com.udea.bancodigital.transactions.application.dto.HistorialTransaccionDto;
import com.udea.bancodigital.transactions.domain.enums.TipoTransaccion;
import com.udea.bancodigital.transactions.domain.model.Transaccion;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;


@Component
public class HistorialTransaccionMapper {
    public HistorialTransaccionDto toDto(Transaccion t) {

        TipoTransaccion tipo = TipoTransaccion.fromId(t.getTipoId());

        return HistorialTransaccionDto.builder()
                .fechaHora(t.getCreatedAt())
                .tipo(tipo != null ? tipo.getDescripcion() : "Desconocido")
                .monto(t.getMonto())
                .build();
    }
}
