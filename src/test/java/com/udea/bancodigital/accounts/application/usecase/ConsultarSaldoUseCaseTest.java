package com.udea.bancodigital.accounts.application.usecase;

import com.udea.bancodigital.accounts.application.dto.ConsultarSaldoResponseDto;
import com.udea.bancodigital.accounts.domain.model.Cuenta;
import com.udea.bancodigital.accounts.domain.model.EstadoCuenta;

import com.udea.bancodigital.accounts.domain.exception.CuentaNoEncontradaException;
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

/*Test 1 Cuenta existente y retorna el valor*/
@Test
void deberiaRetornarSaldoCuandoCuentaEstaActiva() {

    UUID cuentaId = UUID.randomUUID();

    Cuenta cuenta = Cuenta.builder()
            .id(cuentaId)
            .saldo(new BigDecimal("100000"))
            .estado(EstadoCuenta.ACTIVA)
            .build();

    when(cuentaRepository.findById(cuentaId))
            .thenReturn(Optional.of(cuenta));

    ConsultarSaldoResponseDto response = useCase.consultarSaldo(cuentaId);

    assertThat(response.getSaldo()).isEqualTo(new BigDecimal("100000"));
}

/*Test 2 La cuenta no existe*/
@Test
void deberiaLanzarExcepcionCuandoCuentaNoExiste() {

    UUID cuentaId = UUID.randomUUID();

    when(cuentaRepository.findById(cuentaId))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.consultarSaldo(cuentaId))
            .isInstanceOf(CuentaNoEncontradaException.class);
}

/*3 test cuando la cuenta esta incativa */
@Test
void deberiaLanzarExcepcionCuandoCuentaEstaInactiva() {

    UUID cuentaId = UUID.randomUUID();

    Cuenta cuenta = Cuenta.builder()
            .id(cuentaId)
            .saldo(new BigDecimal("100000"))
            .estado(EstadoCuenta.INACTIVA)
            .build();

    when(cuentaRepository.findById(cuentaId))
            .thenReturn(Optional.of(cuenta));

    assertThatThrownBy(() -> useCase.consultarSaldo(cuentaId))
            .isInstanceOf(CuentaInactivaException.class);
}

/*test 4 cuando la cuenta esta bloqueada*/
@Test
void deberiaLanzarExcepcionCuandoCuentaEstaBloqueada() {

    UUID cuentaId = UUID.randomUUID();

    Cuenta cuenta = Cuenta.builder()
            .id(cuentaId)
            .saldo(new BigDecimal("100000"))
            .estado(EstadoCuenta.BLOQUEADA)
            .build();

    when(cuentaRepository.findById(cuentaId))
            .thenReturn(Optional.of(cuenta));

    assertThatThrownBy(() -> useCase.consultarSaldo(cuentaId))
            .isInstanceOf(CuentaInactivaException.class);
}
}
