package com.udea.bancodigital.accounts.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cuentas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CuentaEntity {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String numeroCuenta;

    @Column(nullable = false)
    private UUID clienteId;

    @Column(nullable = false)
    private String tipoCuenta;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal saldo;

    @Column(nullable = false)
    private boolean activa;
}
