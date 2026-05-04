package com.udea.bancodigital.transactions.infrastructure.adapter.in.web;

import com.udea.bancodigital.transactions.application.dto.TransferenciaRequestDto;
import com.udea.bancodigital.transactions.application.dto.TransferenciaResponseDto;
import com.udea.bancodigital.transactions.domain.port.in.TransferirDineroPort;

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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transferencias")
@RequiredArgsConstructor
@Tag(name = "Transferencias", description = "API para transferencias entre cuentas")
public class TransferenciaController {

    private final TransferirDineroPort transferirDineroPort;

    private final AuthenticatedClientProvider authenticatedClientProvider;

    @PostMapping
    @Operation(
            summary = "Transferir dinero",
            description = "Permite transferir dinero entre cuentas activas."
    )
    @ApiResponses(value = {

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Transferencia realizada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos"
            ),

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Cuenta no encontrada"
            ),

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "422",
                    description = "Saldo insuficiente o cuenta inactiva"
            )
    })
    public ResponseEntity<ApiResponse<TransferenciaResponseDto>> transferir(
            @Valid @RequestBody TransferenciaRequestDto request) {

        // Obtener cliente autenticado desde JWT

        UUID clienteId =
                authenticatedClientProvider.getClienteId();

        TransferenciaResponseDto response =
                transferirDineroPort.transferir(
                        request,
                        clienteId.toString()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

}