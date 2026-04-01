package com.udea.bancodigital.accounts.application.usecase;

import com.udea.bancodigital.accounts.application.dto.CrearCuentaRequestDto;
import com.udea.bancodigital.accounts.domain.model.Cuenta;
import com.udea.bancodigital.accounts.domain.port.in.CrearCuentaPort;
import com.udea.bancodigital.accounts.domain.port.out.ClienteServicePort;
import com.udea.bancodigital.accounts.domain.port.out.CuentaRepositoryPort;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;
import com.udea.bancodigital.accounts.domain.exception.ClienteNoEncontradoException;

@RequiredArgsConstructor
public class CrearCuentaUseCase implements CrearCuentaPort {

    private final CuentaRepositoryPort cuentaRepository;
    private final ClienteServicePort clienteService;

    @Override
    public Cuenta crearCuenta(CrearCuentaRequestDto request) {

        // 1. Validar existencia del cliente (Consumiendo el servicio de Clientes)
        if (!clienteService.existeCliente(request.getClienteId())) {
            throw new ClienteNoEncontradoException("No se puede crear la cuenta: El cliente no existe.");
        }

        // 2. Lógica de creación de la cuenta (Tu lógica original intacta)
        Cuenta nuevaCuenta = Cuenta.builder()
                .id(UUID.randomUUID())
                .numeroCuenta(generarNumeroCuenta())
                .clienteId(request.getClienteId())
                .tipoCuenta(request.getTipoCuenta())
                .saldo(BigDecimal.ZERO)
                .activa(true)
                .build();

        return cuentaRepository.save(nuevaCuenta);
    }

    private String generarNumeroCuenta() {
        return "CTA-" + System.currentTimeMillis();
    }
}