package com.udea.bancodigital.transactions.infrastructure.adapter.in.web;

import com.udea.bancodigital.shared.security.AuthenticatedClientProvider;
import com.udea.bancodigital.shared.web.ApiResponse;
import com.udea.bancodigital.transactions.application.dto.TransferenciaRequestDto;
import com.udea.bancodigital.transactions.application.dto.TransferenciaResponseDto;
import com.udea.bancodigital.transactions.domain.port.in.TransferirDineroPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferenciaControllerTest {

    @Mock
    private TransferirDineroPort transferirDineroPort;

    @Mock
    private AuthenticatedClientProvider authenticatedClientProvider;

    @InjectMocks
    private TransferenciaController controller;

    @Test
    void transferir_ShouldReturn201() {
        TransferenciaRequestDto request = new TransferenciaRequestDto();
        UUID clienteId = UUID.randomUUID();
        TransferenciaResponseDto responseDto = TransferenciaResponseDto.builder().estado("COMPLETED").build();

        when(authenticatedClientProvider.getClienteId()).thenReturn(clienteId);
        when(transferirDineroPort.transferir(request, clienteId.toString())).thenReturn(responseDto);

        ResponseEntity<ApiResponse<TransferenciaResponseDto>> response = controller.transferir(request);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody().getData().getEstado()).isEqualTo("COMPLETED");
    }
}
