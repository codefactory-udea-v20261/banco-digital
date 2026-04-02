package com.udea.bancodigital.customers.infrastructure.adapter.in.web;

import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.application.dto.CrearClienteRequestDto;
import com.udea.bancodigital.customers.application.dto.ActualizarClienteRequestDto;
import com.udea.bancodigital.customers.domain.port.in.CrearClientePort;
import com.udea.bancodigital.customers.domain.port.in.ActualizarClientePort;
import com.udea.bancodigital.customers.domain.port.in.ObtenerClientePort;
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

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para la gestión de clientes.
 * Proporciona los endpoints necesarios para el registro, consulta y actualización
 * de los perfiles de clientes en el sistema bancario.
 */
@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "API para la gestión de perfiles de clientes")
public class ClienteController {

    private final CrearClientePort crearClientePort;
    private final ActualizarClientePort actualizarClientePort;
    private final ObtenerClientePort obtenerClientePort;

    /**
     * Registra un nuevo cliente en el sistema.
     *
     * @param request DTO con los datos requeridos para la creación del cliente.
     * @return ResponseEntity con los datos del cliente recién creado.
     */
    @PostMapping
    @Operation(
        summary = "Registrar nuevo cliente",
        description = "Crea un nuevo perfil de cliente. El correo electrónico y el número de documento de identidad deben ser únicos en el sistema."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", 
            description = "Cliente creado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "Datos de entrada inválidos o con formato incorrecto",
            content = @Content(mediaType = "application/json")
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409", 
            description = "Conflicto: El correo electrónico o documento de identidad ya se encuentra registrado",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<ApiResponse<ClienteResponseDto>> crearCliente(
            @Valid @RequestBody CrearClienteRequestDto request) {
        ClienteResponseDto response = crearClientePort.crearCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    /**
     * Consulta la información detallada de un cliente por su identificador único.
     *
     * @param id Identificador único (UUID) del cliente.
     * @return ResponseEntity con la información del cliente solicitado.
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Consultar cliente por ID",
            description = "Obtiene el perfil detallado de un cliente específico utilizando su identificador único."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Información del cliente obtenida exitosamente",
                    ref = "#/components/responses/ClienteOK"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Cliente no encontrado",
                    ref = "#/components/responses/ClienteNotFound"
            )
    })
    public ResponseEntity<ApiResponse<ClienteResponseDto>> obtenerCliente(
            @PathVariable UUID id) {
        ClienteResponseDto response = obtenerClientePort.obtenerPorId(id);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * Actualiza de forma parcial la información de un cliente existente.
     *
     * @param id Identificador único (UUID) del cliente a actualizar.
     * @param request DTO con los campos que se desean modificar.
     * @return ResponseEntity con la información actualizada del cliente.
     */
    @PatchMapping("/{id}")
    @Operation(
            summary = "Actualizar perfil de cliente",
            description = "Actualiza de manera parcial los datos de un cliente. Ciertos campos, como el documento de identidad, son inmutables."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", 
                description = "Cliente actualizado exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400", 
                description = "Datos de actualización inválidos",
                content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", 
                description = "Cliente no encontrado",
                content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "Conflicto: el email ya se encuentra registrado en otro cliente",
                content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<ApiResponse<ClienteResponseDto>> actualizarCliente(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarClienteRequestDto request) {

        ClienteResponseDto response = actualizarClientePort.actualizarCliente(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * Obtiene el listado de las cuentas asociadas a un cliente.
     *
     * @param id Identificador único (UUID) del cliente.
     * @return ResponseEntity con la lista de cuentas del cliente.
     */
    @GetMapping("/{id}/cuentas")
    @Operation(
        summary = "Listar cuentas de un cliente",
        description = "Obtiene un listado de todas las cuentas bancarias asociadas al cliente especificado."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", 
                description = "Lista de cuentas obtenida exitosamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", 
                description = "Cliente no encontrado"
            )
    })
    public ResponseEntity<ApiResponse<List<Object>>> obtenerCuentasCliente(
            @PathVariable UUID id) {
        throw new UnsupportedOperationException("Operación no implementada");
    }
}
