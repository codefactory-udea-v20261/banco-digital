package com.udea.bancodigital.transactions.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.udea.bancodigital.transactions.domain.enums.EstadoTransaccion;

@Entity
@Table(name = "transaccion")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "cuenta_origen_id")
    private UUID cuentaOrigenId;

    @Column(name = "cuenta_destino_id")
    private UUID cuentaDestinoId;

    @Column(name = "tipo_id", nullable = false)
    private Short tipoId;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal monto;

    @Column(name = "saldo_anterior",
            nullable = false,
            precision = 18,
            scale = 2)
    private BigDecimal saldoAnterior;

    @Column(name = "saldo_posterior",
            nullable = false,
            precision = 18,
            scale = 2)
    private BigDecimal saldoPosterior;

    @Column(length = 255)
    private String descripcion;

    @Column(length = 50)
    private String referencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTransaccion estado;

    @Column(name = "created_at",
            nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt =
            OffsetDateTime.now();

    @Column(name = "created_by",
            nullable = false,
            length = 100)
    private String createdBy;

}