package com.udea.bancodigital.customers.infrastructure.adapter.in.web;

import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.application.dto.CrearClienteRequestDto;
import com.udea.bancodigital.customers.application.dto.ActualizarClienteRequestDto;
import com.udea.bancodigital.customers.domain.port.in.CrearClientePort;
import com.udea.bancodigital.customers.domain.port.in.ActualizarClientePort;
import com.udea.bancodigital.customers.domain.port.in.ObtenerClientePort;
import com.udea.bancodigital.customers.domain.port.out.ClienteAccessControlPort;
import com.udea.bancodigital.shared.web.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteControllerSimpleTest {

    @Mock private CrearClientePort crearClientePort;
    @Mock private ActualizarClientePort actualizarClientePort;
    @Mock private ObtenerClientePort obtenerClientePort;
    @Mock private ClienteAccessControlPort clienteAccessControlPort;

    @InjectMocks private ClienteController clienteController;

    @Test
    void crearCliente_Success() {
        CrearClienteRequestDto request = CrearClienteRequestDto.builder().email("test@test.com").build();
        ClienteResponseDto responseDto = ClienteResponseDto.builder().email("test@test.com").build();
        
        when(crearClientePort.crearCliente(any())).thenReturn(responseDto);

        ResponseEntity<ApiResponse<ClienteResponseDto>> response = clienteController.crearCliente(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("test@test.com", response.getBody().getData().getEmail());
    }

    @Test
    void actualizarCliente_Success() {
        UUID id = UUID.randomUUID();
        ActualizarClienteRequestDto request = ActualizarClienteRequestDto.builder().primerNombre("New").build();
        ClienteResponseDto responseDto = ClienteResponseDto.builder().id(id).primerNombre("New").build();

        when(actualizarClientePort.actualizarCliente(eq(id), any())).thenReturn(responseDto);

        ResponseEntity<ApiResponse<ClienteResponseDto>> response = clienteController.actualizarCliente(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New", response.getBody().getData().getPrimerNombre());
    }

    @Test
    void obtenerCuentasCliente_ThrowsException() {
        UUID id = UUID.randomUUID();
        assertThrows(UnsupportedOperationException.class, () -> 
                clienteController.obtenerCuentasCliente(id));
    }
}
