package com.udea.bancodigital.transactions.application.usecase;

import com.udea.bancodigital.accounts.infrastructure.entity.CuentaEntity;
import com.udea.bancodigital.accounts.infrastructure.repository.CuentaJpaRepository;

import com.udea.bancodigital.accounts.domain.exception.CuentaInactivaException;

import com.udea.bancodigital.transactions.application.dto.TransferenciaRequestDto;
import com.udea.bancodigital.transactions.application.dto.TransferenciaResponseDto;

import com.udea.bancodigital.transactions.domain.enums.EstadoTransaccion;
import com.udea.bancodigital.transactions.domain.exception.SaldoInsuficienteException;
import com.udea.bancodigital.transactions.domain.exception.TransferenciaInvalidaException;

import com.udea.bancodigital.transactions.domain.model.Transaccion;
import com.udea.bancodigital.transactions.domain.port.in.TransferirDineroPort;
import com.udea.bancodigital.transactions.domain.port.out.TransaccionRepositoryPort;

import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class TransferirDineroUseCase
        implements TransferirDineroPort {

    private final CuentaJpaRepository cuentaRepository;

    private final TransaccionRepositoryPort transaccionRepository;

    @Override
    @Transactional
    public TransferenciaResponseDto transferir(
            TransferenciaRequestDto request,
            String usuario) {

        String numeroOrigen =
                request.getNumeroCuentaOrigen();

        String numeroDestino =
                request.getNumeroCuentaDestino();

        BigDecimal monto =
                request.getMonto();

        // Validaciones básicas

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw TransferenciaInvalidaException.montoInvalido();
        }

        if (numeroOrigen.equals(numeroDestino)) {
            throw TransferenciaInvalidaException.mismaCuenta();
        }

        // Buscar cuentas

        CuentaEntity origen =
                cuentaRepository
                        .findByNumeroCuenta(numeroOrigen)
                        .orElseThrow(() ->
                                new TransferenciaInvalidaException(
                                        "Cuenta origen no existe"));

        CuentaEntity destino =
                cuentaRepository
                        .findByNumeroCuenta(numeroDestino)
                        .orElseThrow(() ->
                                new TransferenciaInvalidaException(
                                        "Cuenta destino no existe"));

        // Validar estado

        if (!origen.getEstado().equals("ACTIVA")) {
            throw new CuentaInactivaException(origen.getId());
        }

        if (!destino.getEstado().equals("ACTIVA")) {
            throw new CuentaInactivaException(destino.getId());
        }

        // Validar saldo

        if (origen.getSaldo().compareTo(monto) < 0) {
            throw new SaldoInsuficienteException(
                    origen.getId(),
                    origen.getSaldo(),
                    monto);
        }

        BigDecimal saldoAnteriorOrigen =
                origen.getSaldo();

        BigDecimal saldoAnteriorDestino =
                destino.getSaldo();

        // Ejecutar transferencia

        origen.setSaldo(
                origen.getSaldo().subtract(monto));

        destino.setSaldo(
                destino.getSaldo().add(monto));

        cuentaRepository.save(origen);
        cuentaRepository.save(destino);

        String referencia =
                "TRF-" + System.currentTimeMillis();

        // Movimiento DÉBITO

        Transaccion debito =
                Transaccion.builder()
                        .id(UUID.randomUUID())
                        .cuentaOrigenId(origen.getId())
                        .cuentaDestinoId(destino.getId())
                        .tipoId((short) 1)
                        .monto(monto)
                        .saldoAnterior(saldoAnteriorOrigen)
                        .saldoPosterior(origen.getSaldo())
                        .descripcion("Transferencia enviada")
                        .referencia(referencia)
                        .estado(EstadoTransaccion.COMPLETADA)
                        .createdAt(OffsetDateTime.now())
                        .createdBy(usuario != null ? usuario : "SYSTEM")
                        .build();

        // Movimiento CRÉDITO

        Transaccion credito =
                Transaccion.builder()
                        .id(UUID.randomUUID())
                        .cuentaOrigenId(origen.getId())
                        .cuentaDestinoId(destino.getId())
                        .tipoId((short) 1)
                        .monto(monto)
                        .saldoAnterior(saldoAnteriorDestino)
                        .saldoPosterior(destino.getSaldo())
                        .descripcion("Transferencia recibida")
                        .referencia(referencia)
                        .estado(EstadoTransaccion.COMPLETADA)
                        .createdAt(OffsetDateTime.now())
                        .createdBy(usuario != null ? usuario : "SYSTEM")
                        .build();

        Transaccion savedDebito =
                transaccionRepository.save(debito);

        transaccionRepository.save(credito);

        return TransferenciaResponseDto.builder()
                .transaccionId(savedDebito.getId())
                .cuentaOrigenId(origen.getId())
                .cuentaDestinoId(destino.getId())
                .monto(monto)
                .referencia(referencia)
                .estado(savedDebito.getEstado().name())
                .build();
    }

}