package com.udea.bancodigital.accounts.infrastructure.adapter.in.web;

import com.udea.bancodigital.accounts.application.dto.CrearCuentaRequestDto;
import com.udea.bancodigital.accounts.application.dto.CuentaResponseDto;
import com.udea.bancodigital.accounts.application.mapper.CuentaMapper;
import com.udea.bancodigital.accounts.domain.model.Cuenta;
import com.udea.bancodigital.accounts.domain.port.in.ConsultarSaldoPort;
import com.udea.bancodigital.accounts.domain.port.in.CrearCuentaPort;
import com.udea.bancodigital.shared.security.AuthenticatedClientProvider;
import com.udea.bancodigital.shared.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.udea.bancodigital.accounts.application.dto.ConsultarSaldoResponseDto;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cuentas")
@RequiredArgsConstructor
@Tag(name = "Cuentas", description = "API para la gestión de cuentas financieras")
public class CuentaController {

    private final CrearCuentaPort crearCuentaPort;
    private final CuentaMapper cuentaMapper;
    private final ConsultarSaldoPort consultarSaldoPort; 
    private final AuthenticatedClientProvider authenticatedClientProvider;

    @PostMapping
    @PreAuthorize("hasAnyRole('CAJERO', 'ADMIN')")
    @Operation(
            summary = "Crear cuenta financiera",
            description = "Crea una cuenta financiera para un cliente existente y activo."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Cuenta creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Tipo de cuenta inválido o datos de entrada incorrectos",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Cliente no encontrado",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "422",
                    description = "Cliente inactivo",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<ApiResponse<CuentaResponseDto>> crearCuenta(@Valid @RequestBody CrearCuentaRequestDto request) {
        Cuenta cuentaCreada = crearCuentaPort.crearCuenta(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(cuentaMapper.toResponseDto(cuentaCreada)));
    }
    
    @GetMapping("/{id}/saldo")
    @Operation(
            summary = "Consultar saldo de cuenta",
            description = "Permite consultar el saldo de una cuenta si pertenece al cliente autenticado."
    )
    @ApiResponses(value = {
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Saldo consultado exitosamente"
    ),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "La cuenta no pertenece al cliente"
    ),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Cuenta no encontrada"
    ),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "422",
            description = "Cuenta inactiva o bloqueada"
    )
})
    public ResponseEntity<ApiResponse<ConsultarSaldoResponseDto>> consultarSaldo(
            @PathVariable UUID id) {
            
        UUID clienteId = authenticatedClientProvider.getClienteId();
            
        ConsultarSaldoResponseDto response =
                consultarSaldoPort.consultarSaldo(id, clienteId);
            
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
