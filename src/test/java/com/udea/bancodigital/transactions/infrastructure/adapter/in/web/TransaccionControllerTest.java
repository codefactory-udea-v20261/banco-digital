package com.udea.bancodigital.transactions.infrastructure.adapter.in.web;

import com.udea.bancodigital.shared.web.ApiResponse;
import com.udea.bancodigital.transactions.application.dto.HistorialTransaccionDto;
import com.udea.bancodigital.transactions.application.dto.RetiroRequestDto;
import com.udea.bancodigital.transactions.application.usecase.ConsultarHistorialUseCase;
import com.udea.bancodigital.transactions.application.usecase.RealizarRetiroUseCase;
import com.udea.bancodigital.transactions.domain.model.Transaccion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransaccionControllerTest {

    @Mock
    private RealizarRetiroUseCase realizarRetiroUseCase;

    @Mock
    private ConsultarHistorialUseCase consultarHistorialUseCase;

    @InjectMocks
    private TransaccionController controller;

    @Test
    void retirar_ShouldReturn200() {
        RetiroRequestDto request = new RetiroRequestDto();
        Transaccion expectedResponse = Transaccion.builder().id(UUID.randomUUID()).build();

        when(realizarRetiroUseCase.ejecutar(request)).thenReturn(expectedResponse);

        ResponseEntity<ApiResponse<Transaccion>> response = controller.retirar(request);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getData().getId()).isEqualTo(expectedResponse.getId());
    }

    @Test
    void consultarHistorial_ShouldReturn200() {
        UUID cuentaId = UUID.randomUUID();
        HistorialTransaccionDto dto = HistorialTransaccionDto.builder().tipo("RETIRO").build();
        when(consultarHistorialUseCase.ejecutar(cuentaId)).thenReturn(List.of(dto));

        ResponseEntity<ApiResponse<List<HistorialTransaccionDto>>> response = controller.consultarHistorial(cuentaId);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getData()).hasSize(1);
    }
}
