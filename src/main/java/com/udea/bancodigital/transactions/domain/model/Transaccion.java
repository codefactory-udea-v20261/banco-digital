package com.udea.bancodigital.transactions.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.udea.bancodigital.transactions.domain.enums.EstadoTransaccion;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {

    private UUID id;

    private UUID cuentaOrigenId;

    private UUID cuentaDestinoId;

    private Short tipoId;

    private BigDecimal monto;

    private BigDecimal saldoAnterior;

    private BigDecimal saldoPosterior;

    private String descripcion;

    private String referencia;

    private EstadoTransaccion estado;
    
    private OffsetDateTime createdAt;

    private String createdBy;

}