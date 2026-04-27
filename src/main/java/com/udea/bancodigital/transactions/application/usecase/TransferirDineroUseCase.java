package com.udea.bancodigital.transactions.application.usecase;

import com.udea.bancodigital.accounts.infrastructure.entity.CuentaEntity;
import com.udea.bancodigital.accounts.infrastructure.repository.CuentaJpaRepository;

import com.udea.bancodigital.transactions.application.dto.TransferenciaRequestDto;
import com.udea.bancodigital.transactions.application.dto.TransferenciaResponseDto;

import com.udea.bancodigital.transactions.domain.enums.EstadoTransaccion;
import com.udea.bancodigital.transactions.domain.model.Transaccion;
import com.udea.bancodigital.transactions.domain.port.in.TransferirDineroPort;
import com.udea.bancodigital.transactions.domain.port.out.TransaccionRepositoryPort;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

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

    if (monto.compareTo(BigDecimal.ZERO) <= 0) {
        throw new RuntimeException("Monto inválido");
    }

    if (numeroOrigen.equals(numeroDestino)) {
        throw new RuntimeException(
                "No puede transferir a la misma cuenta");
    }

    CuentaEntity origen =
            cuentaRepository
                    .findByNumeroCuenta(numeroOrigen)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Cuenta origen no existe"));

    CuentaEntity destino =
            cuentaRepository
                    .findByNumeroCuenta(numeroDestino)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Cuenta destino no existe"));

    if (!origen.getEstado().equals("ACTIVA")) {
        throw new RuntimeException(
                "Cuenta origen inactiva");
    }

    if (!destino.getEstado().equals("ACTIVA")) {
        throw new RuntimeException(
                "Cuenta destino inactiva");
    }

    if (origen.getSaldo().compareTo(monto) < 0) {
        throw new RuntimeException(
                "Saldo insuficiente");
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

    // Movimiento DÉBITO (origen)

    Transaccion debito =
            Transaccion.builder()
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
                    .createdBy(usuario)
                    .build();

    // Movimiento CRÉDITO (destino)

    Transaccion credito =
            Transaccion.builder()
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
                    .createdBy(usuario)
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