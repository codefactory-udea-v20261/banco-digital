package com.udea.bancodigital.transactions.infrastructure.adapter.out.persistence;

import com.udea.bancodigital.accounts.domain.model.Cuenta;
import com.udea.bancodigital.accounts.domain.port.out.CuentaRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaPersistenceAdapterTest {

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @InjectMocks
    private CuentaPersistenceAdapter adapter;

    @Test
    void consultarSaldo_ShouldReturnSaldoIfExists() {
        UUID cuentaId = UUID.randomUUID();
        Cuenta cuenta = Cuenta.builder().id(cuentaId).saldo(new BigDecimal("150.0")).build();
        when(cuentaRepositoryPort.findById(cuentaId)).thenReturn(Optional.of(cuenta));

        Optional<BigDecimal> result = adapter.consultarSaldo(cuentaId);

        assertThat(result).isPresent().contains(new BigDecimal("150.0"));
    }

    @Test
    void actualizarSaldo_ShouldSaveWithNewSaldo() {
        UUID cuentaId = UUID.randomUUID();
        Cuenta cuenta = Cuenta.builder().id(cuentaId).saldo(new BigDecimal("150.0")).build();
        when(cuentaRepositoryPort.findById(cuentaId)).thenReturn(Optional.of(cuenta));

        adapter.actualizarSaldo(cuentaId, new BigDecimal("100.0"));

        verify(cuentaRepositoryPort).save(argThat(c -> c.getSaldo().equals(new BigDecimal("100.0"))));
    }

    @Test
    void existeCuenta_ShouldReturnTrueIfExists() {
        UUID cuentaId = UUID.randomUUID();
        when(cuentaRepositoryPort.findById(cuentaId)).thenReturn(Optional.of(Cuenta.builder().build()));

        boolean exists = adapter.existeCuenta(cuentaId);

        assertThat(exists).isTrue();
    }
}
