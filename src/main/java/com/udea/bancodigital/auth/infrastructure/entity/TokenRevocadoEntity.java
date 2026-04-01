package com.udea.bancodigital.auth.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "token_revocado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenRevocadoEntity {

    @Id
    @Column(nullable = false, unique = true)
    private String jti;

    @Column(nullable = false)
    private Long expirationTime;
}
