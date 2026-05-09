package com.udea.bancodigital.transactions.infrastructure.adapter.out.persistence;

import com.udea.bancodigital.accounts.domain.model.Cuenta;
import com.udea.bancodigital.accounts.domain.port.out.CuentaRepositoryPort;
import com.udea.bancodigital.transactions.domain.port.out.CuentaServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CuentaPersistenceAdapter implements CuentaServicePort {
    // Inyectamos el puerto del otro módulo, no el repositorio directo
    private final CuentaRepositoryPort cuentaRepositoryPort;

    @Override
    public Optional<BigDecimal> consultarSaldo(UUID cuentaId) {
        return cuentaRepositoryPort.findById(cuentaId)
                .map(Cuenta::getSaldo); // Usamos el modelo de dominio Cuenta
    }

    @Override
    public void actualizarSaldo(UUID cuentaId, BigDecimal nuevoSaldo) {
        cuentaRepositoryPort.findById(cuentaId).ifPresent(cuenta -> {

            // Creamos una nueva instancia evolucionada:
            Cuenta cuentaActualizada = cuenta.withSaldo(nuevoSaldo);

            // Guardamos la nueva instancia
            cuentaRepositoryPort.save(cuentaActualizada);
        });
    }

    @Override
    public boolean existeCuenta(UUID cuentaId) {
        return cuentaRepositoryPort.findById(cuentaId).isPresent();
    }
}
