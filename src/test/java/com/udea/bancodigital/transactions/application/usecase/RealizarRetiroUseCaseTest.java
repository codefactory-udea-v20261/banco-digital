package com.udea.bancodigital.transactions.application.usecase;

import com.udea.bancodigital.transactions.application.dto.RetiroRequestDto;
import com.udea.bancodigital.transactions.domain.exception.CuentaTransaccionException;
import com.udea.bancodigital.transactions.domain.exception.SaldoInsuficienteException;
import com.udea.bancodigital.transactions.domain.model.Transaccion;
import com.udea.bancodigital.transactions.domain.port.out.CuentaServicePort;
import com.udea.bancodigital.transactions.domain.port.out.TransaccionRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RealizarRetiroUseCaseTest {

    @Mock
    private TransaccionRepositoryPort transaccionRepository;

    @Mock
    private CuentaServicePort cuentaService;

    @InjectMocks
    private RealizarRetiroUseCase useCase;

    @Test
    void shouldExecuteRetiroSuccessfully() {
        UUID cuentaId = UUID.randomUUID();
        RetiroRequestDto request = new RetiroRequestDto(cuentaId, new BigDecimal("50.0"), "Test Retiro");
        
        when(cuentaService.consultarSaldo(cuentaId)).thenReturn(Optional.of(new BigDecimal("100.0")));
        Transaccion expectedTx = Transaccion.builder().id(UUID.randomUUID()).build();
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(expectedTx);

        Transaccion result = useCase.ejecutar(request);

        assertThat(result).isNotNull();
        verify(cuentaService).actualizarSaldo(cuentaId, new BigDecimal("50.0"));
        verify(transaccionRepository).save(any(Transaccion.class));
    }

    @Test
    void shouldThrowExceptionWhenCuentaNotFound() {
        UUID cuentaId = UUID.randomUUID();
        RetiroRequestDto request = new RetiroRequestDto(cuentaId, new BigDecimal("50.0"), "Test Retiro");
        
        when(cuentaService.consultarSaldo(cuentaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.ejecutar(request))
                .isInstanceOf(CuentaTransaccionException.class);
    }

    @Test
    void shouldThrowExceptionWhenSaldoInsuficiente() {
        UUID cuentaId = UUID.randomUUID();
        RetiroRequestDto request = new RetiroRequestDto(cuentaId, new BigDecimal("150.0"), "Test Retiro");
        
        when(cuentaService.consultarSaldo(cuentaId)).thenReturn(Optional.of(new BigDecimal("100.0")));

        assertThatThrownBy(() -> useCase.ejecutar(request))
                .isInstanceOf(SaldoInsuficienteException.class);
    }
}
