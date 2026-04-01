package com.udea.bancodigital.accounts.application.usecase;

import com.udea.bancodigital.accounts.application.dto.CrearCuentaRequestDto;
import com.udea.bancodigital.accounts.domain.exception.ClienteNoEncontradoException;
import com.udea.bancodigital.accounts.domain.exception.TipoCuentaInvalidoException;
import com.udea.bancodigital.accounts.domain.model.Cuenta;
import com.udea.bancodigital.accounts.domain.model.TipoCuenta;
import com.udea.bancodigital.accounts.domain.port.in.CrearCuentaPort;
import com.udea.bancodigital.accounts.domain.port.out.ClienteServicePort;
import com.udea.bancodigital.accounts.domain.port.out.CuentaRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

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

        TipoCuenta tipoCuenta = parseTipoCuenta(request.getTipoCuenta());
        Cuenta nuevaCuenta = Cuenta.crearNueva(
                request.getClienteId(),
                tipoCuenta,
                generarNumeroCuenta()
        );

        return cuentaRepository.save(nuevaCuenta);
    }

    private String generarNumeroCuenta() {
        return "CTA-" + System.currentTimeMillis();
    }

    private TipoCuenta parseTipoCuenta(String tipoCuenta) {
        try {
            return TipoCuenta.fromNombre(tipoCuenta);
        } catch (IllegalArgumentException ex) {
            throw new TipoCuentaInvalidoException(tipoCuenta);
        }
    }
}
