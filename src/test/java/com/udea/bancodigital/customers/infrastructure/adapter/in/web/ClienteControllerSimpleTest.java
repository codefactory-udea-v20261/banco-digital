package com.udea.bancodigital.customers.infrastructure.adapter.in.web;

import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.domain.port.in.ActualizarClientePort;
import com.udea.bancodigital.customers.domain.port.in.CrearClientePort;
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

@ExtendWith(MockitoExtension.class)
class ClienteControllerSimpleTest {

    @Mock private CrearClientePort crearClientePort;
    @Mock private ActualizarClientePort actualizarClientePort;
    @Mock private ObtenerClientePort obtenerClientePort;
    @Mock private ClienteAccessControlPort clienteAccessControlPort;

    @InjectMocks private ClienteController clienteController;

    @Test
    void obtenerCuentasCliente_ThrowsException() {
        UUID id = UUID.randomUUID();
        assertThrows(UnsupportedOperationException.class, () -> 
                clienteController.obtenerCuentasCliente(id));
    }
}
