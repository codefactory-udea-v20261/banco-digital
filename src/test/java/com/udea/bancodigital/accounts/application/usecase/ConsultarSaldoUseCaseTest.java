package com.udea.bancodigital.accounts.application.usecase;

import com.udea.bancodigital.accounts.application.dto.ConsultarSaldoResponseDto;
import com.udea.bancodigital.accounts.domain.model.Cuenta;
import com.udea.bancodigital.accounts.domain.model.EstadoCuenta;

import com.udea.bancodigital.accounts.domain.exception.CuentaNoEncontradaException;
import com.udea.bancodigital.accounts.domain.exception.CuentaNoPerteneceAlClienteException;
import com.udea.bancodigital.accounts.domain.exception.CuentaInactivaException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.udea.bancodigital.accounts.domain.port.out.CuentaRepositoryPort;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ConsultarSaldoUseCaseTest {

    @Mock
    private CuentaRepositoryPort cuentaRepository;

    @InjectMocks
    private ConsultarSaldoUseCase useCase;

    /* Test 1: Cuenta activa y pertenece al cliente */
    @Test
    void deberiaRetornarSaldoCuandoCuentaEstaActiva() {

        UUID cuentaId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();

        Cuenta cuenta = Cuenta.builder()
                .id(cuentaId)
                .clienteId(clienteId) // 🔥 IMPORTANTE
                .saldo(new BigDecimal("100000"))
                .estado(EstadoCuenta.ACTIVA)
                .build();

        when(cuentaRepository.findById(cuentaId))
                .thenReturn(Optional.of(cuenta));

        ConsultarSaldoResponseDto response =
                useCase.consultarSaldo(cuentaId, clienteId);

        assertThat(response.getSaldo())
                .isEqualTo(new BigDecimal("100000"));
    }

    /* Test 2: Cuenta no existe */
    @Test
    void deberiaLanzarExcepcionCuandoCuentaNoExiste() {

        UUID cuentaId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();

        when(cuentaRepository.findById(cuentaId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.consultarSaldo(cuentaId, clienteId))
                .isInstanceOf(CuentaNoEncontradaException.class);
    }

    /* Test 3: Cuenta inactiva */
    @Test
    void deberiaLanzarExcepcionCuandoCuentaEstaInactiva() {

        UUID cuentaId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();

        Cuenta cuenta = Cuenta.builder()
                .id(cuentaId)
                .clienteId(clienteId)
                .saldo(new BigDecimal("100000"))
                .estado(EstadoCuenta.INACTIVA)
                .build();

        when(cuentaRepository.findById(cuentaId))
                .thenReturn(Optional.of(cuenta));

        assertThatThrownBy(() -> useCase.consultarSaldo(cuentaId, clienteId))
                .isInstanceOf(CuentaInactivaException.class);
    }

    /* Test 4: Cuenta bloqueada */
    @Test
    void deberiaLanzarExcepcionCuandoCuentaEstaBloqueada() {

        UUID cuentaId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();

        Cuenta cuenta = Cuenta.builder()
                .id(cuentaId)
                .clienteId(clienteId)
                .saldo(new BigDecimal("100000"))
                .estado(EstadoCuenta.BLOQUEADA)
                .build();

        when(cuentaRepository.findById(cuentaId))
                .thenReturn(Optional.of(cuenta));

        assertThatThrownBy(() -> useCase.consultarSaldo(cuentaId, clienteId))
                .isInstanceOf(CuentaInactivaException.class);
    }

    /* Test 5: Cuenta NO pertenece al cliente */
    @Test
    void deberiaLanzarExcepcionCuandoCuentaNoPerteneceAlCliente() {

        UUID cuentaId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID otroClienteId = UUID.randomUUID();

        Cuenta cuenta = Cuenta.builder()
                .id(cuentaId)
                .clienteId(otroClienteId) 
                .saldo(new BigDecimal("100000"))
                .estado(EstadoCuenta.ACTIVA)
                .build();

        when(cuentaRepository.findById(cuentaId))
                .thenReturn(Optional.of(cuenta));

        assertThatThrownBy(() -> useCase.consultarSaldo(cuentaId, clienteId))
        .isInstanceOf(CuentaNoPerteneceAlClienteException.class);
    }
}