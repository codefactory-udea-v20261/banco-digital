package com.udea.bancodigital.customers.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entidad de DOMINIO — Cliente.
 *
 * REGLA ABSOLUTA (ADR-001): Esta clase NO tiene anotaciones de Spring, JPA ni Lombok
 * que generen dependencias de framework. Es un POJO puro.
 *
 * No confundir con ClienteEntity.java (infrastructure/entity/) que sí tiene @Entity.
 */
@Getter
@Builder
@With
public class Cliente {

    private final UUID id;
    private final String numeroCedula;       // inmutable — HU3
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String email;
    private String telefono;
    private final LocalDate fechaNacimiento;
    private boolean activo;
}


