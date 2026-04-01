package com.udea.bancodigital.accounts.infrastructure.entity;

import com.udea.bancodigital.shared.util.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cuenta")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CuentaEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "numero_cuenta", unique = true, nullable = false, length = 20)
    private String numeroCuenta;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Column(name = "tipo_cuenta_id", nullable = false)
    private Short tipoCuentaId;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal saldo;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String estado = "ACTIVA";

    @Column(name = "fecha_apertura", nullable = false)
    @Builder.Default
    private LocalDate fechaApertura = LocalDate.now();
}
