package com.udea.bancodigital.customers.infrastructure.adapter.in.web;

import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.application.dto.CrearClienteRequestDto;
import com.udea.bancodigital.customers.domain.port.in.CrearClientePort;
import com.udea.bancodigital.shared.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST — Módulo Customers.
 *
 * REGLA: El controlador NO contiene lógica de negocio.
 * Solo: recibe request → llama al puerto → retorna ApiResponse.
 *
 * HU1: POST  /api/v1/clientes        (Santiago)
 * HU2: GET   /api/v1/clientes/{id}   (Santiago — con HATEOAS)
 * HU3: PATCH /api/v1/clientes/{id}   (Carlos)
 * HU5: GET   /api/v1/clientes/{id}/cuentas (Manuel)
 */
@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gestión de clientes del banco digital")
public class ClienteController {

    private final CrearClientePort crearClientePort;
    // TODO Sprint 1: inyectar puertos de HU2, HU3, HU5

    // ── HU1 — Registro de nuevo cliente ────────────────────────────────────
    @PostMapping
    @Operation(
        summary = "HU1 — Registrar nuevo cliente",
        description = "Crea un nuevo cliente en el sistema. El email y la cédula deben ser únicos."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "El cliente ya existe (email o cédula duplicada)")
    })
    public ResponseEntity<ApiResponse<ClienteResponseDto>> crearCliente(
            @Valid @RequestBody CrearClienteRequestDto request) {
        ClienteResponseDto response = crearClientePort.crearCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    // ── HU2 — Consulta cliente por ID (HATEOAS) ─────────────────────────────
    @GetMapping("/{id}")
    @Operation(summary = "HU2 — Consultar cliente por ID")
    public ResponseEntity<ApiResponse<ClienteResponseDto>> obtenerCliente(
            @PathVariable java.util.UUID id) {
        // TODO Sprint 1 — Santiago: implementar con HATEOAS links
        throw new UnsupportedOperationException("TODO: Sprint 1 — HU2");
    }

    // ── HU3 — Actualización parcial de cliente ──────────────────────────────
    @PatchMapping("/{id}")
    @Operation(summary = "HU3 — Actualizar cliente (parcial) — cédula es inmutable")
    public ResponseEntity<ApiResponse<ClienteResponseDto>> actualizarCliente(
            @PathVariable java.util.UUID id,
            @Valid @RequestBody Object request) {
        // TODO Sprint 1 — Carlos: implementar con validación de campos inmutables
        throw new UnsupportedOperationException("TODO: Sprint 1 — HU3");
    }

    // ── HU5 — Consulta cuentas por cliente ──────────────────────────────────
    @GetMapping("/{id}/cuentas")
    @Operation(summary = "HU5 — Listar cuentas de un cliente")
    public ResponseEntity<ApiResponse<java.util.List<Object>>> obtenerCuentasCliente(
            @PathVariable java.util.UUID id) {
        // TODO Sprint 1 — Manuel: implementar
        throw new UnsupportedOperationException("TODO: Sprint 1 — HU5");
    }
}
