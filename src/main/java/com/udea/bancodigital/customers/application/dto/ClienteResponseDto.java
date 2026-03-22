package com.udea.bancodigital.customers.application.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

/**
 * HU1/HU2 — DTO de respuesta para cliente.
 * Extiende RepresentationModel para soportar HATEOAS (HU2 — GET /clientes/:id).
 *
 * NUNCA retornar la entidad JPA directamente — ADR-001, CODING_STANDARDS sección 3.
 */
@Getter
@Builder
public class ClienteResponseDto extends RepresentationModel<ClienteResponseDto> {

    private UUID id;
    private String numeroCedula;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String email;
    private String telefono;
    private LocalDate fechaNacimiento;
    private boolean activo;
    private Instant createdAt;
}
