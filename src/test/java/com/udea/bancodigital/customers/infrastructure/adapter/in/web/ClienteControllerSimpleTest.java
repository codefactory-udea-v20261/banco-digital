package com.udea.bancodigital.customers.infrastructure.adapter.in.web;

import com.udea.bancodigital.accounts.application.dto.CuentaResponseDto;
import com.udea.bancodigital.accounts.domain.port.in.ListarCuentasClientePort;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteControllerSimpleTest {

    @Mock private CrearClientePort crearClientePort;
    @Mock private ActualizarClientePort actualizarClientePort;
    @Mock private ObtenerClientePort obtenerClientePort;
    @Mock private ClienteAccessControlPort clienteAccessControlPort;
    @Mock private ListarCuentasClientePort listarCuentasClientePort;

    @InjectMocks private ClienteController clienteController;

    @Test
    void obtenerCuentasCliente_DeberiaRetornarListaDeCuentas() {
        UUID clienteId = UUID.randomUUID();
        CuentaResponseDto cuenta = CuentaResponseDto.builder()
                .id(UUID.randomUUID())
                .clienteId(clienteId)
                .numeroCuenta("CTA-1234567890")
                .tipoCuenta("AHORRO")
                .saldo(new BigDecimal("100000"))
                .estado("ACTIVA")
                .fechaApertura(LocalDate.now())
                .build();

        doNothing().when(clienteAccessControlPort).validateCanView(clienteId);
        when(listarCuentasClientePort.listarPorClienteId(clienteId)).thenReturn(List.of(cuenta));

        ResponseEntity<ApiResponse<List<CuentaResponseDto>>> response =
                clienteController.obtenerCuentasCliente(clienteId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        assertEquals("CTA-1234567890", response.getBody().getData().get(0).getNumeroCuenta());
        verify(clienteAccessControlPort).validateCanView(clienteId);
        verify(listarCuentasClientePort).listarPorClienteId(clienteId);
    }
}
