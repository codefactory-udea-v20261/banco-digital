package com.udea.bancodigital.customers.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entidad de DOMINIO — Cliente.
 *
 * Esta clase es un POJO puro y no tiene anotaciones de Spring o JPA
 * que generen dependencias de framework. Es un POJO puro.
 *
 * No confundir con ClienteEntity.java (infrastructure/entity/) que sí tiene @Entity.
 */
@Getter
@Builder
@With
public class Cliente {

    private final UUID id;
    private final NumeroCedula numeroCedula;  // Value Object inmutable
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private Email email;                      // Value Object con validación de dominio
    private String telefono;
    private final LocalDate fechaNacimiento;  // inmutable
    private boolean activo;
}


