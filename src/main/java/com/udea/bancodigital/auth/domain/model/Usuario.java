package com.udea.bancodigital.auth.domain.model;

import lombok.Builder;
import lombok.Value;
import java.util.Set;

@Value
@Builder
public class Usuario {
    Long id;
    String correo;
    String clave;
    boolean activo;
    boolean bloqueado;
    Integer intentosFallidos;
    String secretoMfa;
    Set<Rol> roles;
}
