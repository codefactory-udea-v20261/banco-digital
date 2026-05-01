package com.udea.bancodigital.transactions.infrastructure.adapter.in.web;

import com.udea.bancodigital.shared.web.ApiResponse;
import com.udea.bancodigital.transactions.application.dto.RetiroRequestDto;
import com.udea.bancodigital.transactions.application.usecase.RealizarRetiroUseCase;
import com.udea.bancodigital.transactions.domain.model.Transaccion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.udea.bancodigital.transactions.application.dto.HistorialTransaccionDto;
import com.udea.bancodigital.transactions.application.usecase.ConsultarHistorialUseCase;
import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para la gestión de transacciones financieras.
 * Proporciona los endpoints necesarios para realizar retiros, transferencias
 * y consultas de movimientos en el sistema core-banking.
 */
@RestController
@RequestMapping("/api/v1/transacciones")
@RequiredArgsConstructor
@Tag(name = "Transacciones", description = "API para operaciones financieras y movimientos de cuenta")
public class TransaccionController {
    private final RealizarRetiroUseCase realizarRetiroUseCase;
    private final ConsultarHistorialUseCase consultarHistorialUseCase;

    /**
     * Procesa un retiro de dinero de una cuenta específica.
     *
     * @param request DTO con la cuenta, el monto y la descripción del retiro.
     * @return ResponseEntity con los detalles de la transacción realizada.
     */
    @PostMapping("/retiro")
    @Operation(
            summary = "Realizar un retiro de dinero",
            description = "Permite disminuir el saldo de una cuenta activa. Verifica saldo suficiente y actualiza el estado contable de la cuenta."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Retiro procesado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos (monto negativo o datos faltantes)",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "La cuenta especificada no existe",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "422",
                    description = "Saldo insuficiente para realizar la operación",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<ApiResponse<Transaccion>> retirar(
            @Valid @RequestBody RetiroRequestDto request) {

        Transaccion response = realizarRetiroUseCase.ejecutar(request);

        // Usamos ApiResponse.ok() para mantener la consistencia con ClienteController
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/historial/{cuentaId}")
    @Operation(
            summary = "Consultar historial de movimientos",
            description = "Retorna las transacciones de una cuenta ordenadas de la más reciente a la más antigua"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Historial consultado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "La cuenta no existe",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<ApiResponse<List<HistorialTransaccionDto>>> consultarHistorial(
            @PathVariable UUID cuentaId) {

        List<HistorialTransaccionDto> response =
                consultarHistorialUseCase.ejecutar(cuentaId);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

}
