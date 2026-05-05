package com.udea.bancodigital.transactions.application.usecase;

import com.udea.bancodigital.accounts.domain.exception.CuentaInactivaException;
import com.udea.bancodigital.accounts.infrastructure.entity.CuentaEntity;
import com.udea.bancodigital.accounts.infrastructure.repository.CuentaJpaRepository;
import com.udea.bancodigital.transactions.application.dto.TransferenciaRequestDto;
import com.udea.bancodigital.transactions.application.dto.TransferenciaResponseDto;
import com.udea.bancodigital.transactions.domain.enums.EstadoTransaccion;
import com.udea.bancodigital.transactions.domain.exception.SaldoInsuficienteException;
import com.udea.bancodigital.transactions.domain.exception.TransferenciaInvalidaException;
import com.udea.bancodigital.transactions.domain.model.Transaccion;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferirDineroUseCaseTest {

    @Mock
    private CuentaJpaRepository cuentaRepository;

    @Mock
    private TransaccionRepositoryPort transaccionRepository;

    @InjectMocks
    private TransferirDineroUseCase useCase;

    @Test
    void shouldExecuteTransferSuccessfully() {
        TransferenciaRequestDto request = new TransferenciaRequestDto("123", "456", new BigDecimal("50.0"));
        
        CuentaEntity origen = new CuentaEntity();
        origen.setId(UUID.randomUUID());
        origen.setNumeroCuenta("123");
        origen.setEstado("ACTIVA");
        origen.setSaldo(new BigDecimal("100.0"));

        CuentaEntity destino = new CuentaEntity();
        destino.setId(UUID.randomUUID());
        destino.setNumeroCuenta("456");
        destino.setEstado("ACTIVA");
        destino.setSaldo(new BigDecimal("20.0"));

        when(cuentaRepository.findByNumeroCuenta("123")).thenReturn(Optional.of(origen));
        when(cuentaRepository.findByNumeroCuenta("456")).thenReturn(Optional.of(destino));

        Transaccion debito = Transaccion.builder().id(UUID.randomUUID()).estado(EstadoTransaccion.COMPLETADA).build();
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(debito);

        TransferenciaResponseDto response = useCase.transferir(request, "user1");

        assertThat(response).isNotNull();
        assertThat(response.getMonto()).isEqualTo(new BigDecimal("50.0"));
        assertThat(response.getReferencia().length()).isLessThanOrEqualTo(50);
        assertThat(origen.getSaldo()).isEqualTo(new BigDecimal("50.0"));
        assertThat(destino.getSaldo()).isEqualTo(new BigDecimal("70.0"));

        verify(cuentaRepository, times(2)).save(any(CuentaEntity.class));
        verify(transaccionRepository, times(2)).save(any(Transaccion.class));
    }

    @Test
    void shouldThrowExceptionWhenMontoInvalid() {
        TransferenciaRequestDto request = new TransferenciaRequestDto("123", "456", new BigDecimal("-50.0"));
        assertThatThrownBy(() -> useCase.transferir(request, "user1"))
                .isInstanceOf(TransferenciaInvalidaException.class)
                .hasMessageContaining("mayor a cero");
    }

    @Test
    void shouldThrowExceptionWhenSameAccount() {
        TransferenciaRequestDto request = new TransferenciaRequestDto("123", "123", new BigDecimal("50.0"));
        assertThatThrownBy(() -> useCase.transferir(request, "user1"))
                .isInstanceOf(TransferenciaInvalidaException.class)
                .hasMessageContaining("origen y destino");
    }

    @Test
    void shouldThrowExceptionWhenOrigenNotFound() {
        TransferenciaRequestDto request = new TransferenciaRequestDto("123", "456", new BigDecimal("50.0"));
        when(cuentaRepository.findByNumeroCuenta("123")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.transferir(request, "user1"))
                .isInstanceOf(TransferenciaInvalidaException.class)
                .hasMessageContaining("origen no existe");
    }

    @Test
    void shouldThrowExceptionWhenDestinoNotFound() {
        TransferenciaRequestDto request = new TransferenciaRequestDto("123", "456", new BigDecimal("50.0"));
        CuentaEntity origen = new CuentaEntity();
        when(cuentaRepository.findByNumeroCuenta("123")).thenReturn(Optional.of(origen));
        when(cuentaRepository.findByNumeroCuenta("456")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.transferir(request, "user1"))
                .isInstanceOf(TransferenciaInvalidaException.class)
                .hasMessageContaining("destino no existe");
    }

    @Test
    void shouldThrowExceptionWhenCuentaInactiva() {
        TransferenciaRequestDto request = new TransferenciaRequestDto("123", "456", new BigDecimal("50.0"));
        CuentaEntity origen = new CuentaEntity();
        origen.setEstado("BLOQUEADA");
        when(cuentaRepository.findByNumeroCuenta("123")).thenReturn(Optional.of(origen));
        CuentaEntity destino = new CuentaEntity();
        when(cuentaRepository.findByNumeroCuenta("456")).thenReturn(Optional.of(destino));

        assertThatThrownBy(() -> useCase.transferir(request, "user1"))
                .isInstanceOf(CuentaInactivaException.class);
    }

    @Test
    void shouldThrowExceptionWhenSaldoInsuficiente() {
        TransferenciaRequestDto request = new TransferenciaRequestDto("123", "456", new BigDecimal("500.0"));
        CuentaEntity origen = new CuentaEntity();
        origen.setEstado("ACTIVA");
        origen.setSaldo(new BigDecimal("100.0"));
        when(cuentaRepository.findByNumeroCuenta("123")).thenReturn(Optional.of(origen));
        CuentaEntity destino = new CuentaEntity();
        destino.setEstado("ACTIVA");
        when(cuentaRepository.findByNumeroCuenta("456")).thenReturn(Optional.of(destino));

        assertThatThrownBy(() -> useCase.transferir(request, "user1"))
                .isInstanceOf(SaldoInsuficienteException.class);
    }
}
