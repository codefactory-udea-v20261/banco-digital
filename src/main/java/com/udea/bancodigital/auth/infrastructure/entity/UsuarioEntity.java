package com.udea.bancodigital.auth.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioEntity {

    @Id
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    private String correo;

    @Column(name = "password_hash", nullable = false)
    private String clave;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    @Column(name = "intentos_fallidos")
    @Builder.Default
    private Short intentosFallidos = 0;

    @Column(name = "mfa_secret")
    private String secretoMfa;

    @Column(name = "bloqueado_hasta")
    private OffsetDateTime bloqueadoHasta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private RolEntity rol;

    public boolean isBloqueado() {
        return bloqueadoHasta != null;
    }
}
