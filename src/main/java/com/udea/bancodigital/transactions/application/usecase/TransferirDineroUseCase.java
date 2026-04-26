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

        UUID origenId = request.getCuentaOrigenId();
        UUID destinoId = request.getCuentaDestinoId();
        BigDecimal monto = request.getMonto();

        // Validar monto

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Monto inválido");
        }

        // Validar misma cuenta

        if (origenId.equals(destinoId)) {
            throw new RuntimeException(
                    "No puede transferir a la misma cuenta");
        }

        // Buscar cuentas

        CuentaEntity origen =
                cuentaRepository.findById(origenId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Cuenta origen no existe"));

        CuentaEntity destino =
                cuentaRepository.findById(destinoId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Cuenta destino no existe"));

        // Validar cuentas activas

        if (!origen.getEstado().equals("ACTIVA")) {
            throw new RuntimeException(
                    "Cuenta origen inactiva");
        }

        if (!destino.getEstado().equals("ACTIVA")) {
            throw new RuntimeException(
                    "Cuenta destino inactiva");
        }

        // Validar saldo

        if (origen.getSaldo().compareTo(monto) < 0) {
            throw new RuntimeException(
                    "Saldo insuficiente");
        }

        // Guardar saldo anterior

        BigDecimal saldoAnterior =
                origen.getSaldo();

        // Realizar transferencia

        origen.setSaldo(
                origen.getSaldo().subtract(monto));

        destino.setSaldo(
                destino.getSaldo().add(monto));

        cuentaRepository.save(origen);
        cuentaRepository.save(destino);

        // Generar referencia

        String referencia =
                "TRF-" + System.currentTimeMillis();

        // Crear transacción

        Transaccion transaccion =
                Transaccion.builder()
                        .id(UUID.randomUUID())
                        .cuentaOrigenId(origenId)
                        .cuentaDestinoId(destinoId)
                        .tipoId((short) 1)
                        .monto(monto)
                        .saldoAnterior(saldoAnterior)
                        .saldoPosterior(origen.getSaldo())
                        .descripcion("Transferencia bancaria")
                        .referencia(referencia)
                        .estado(EstadoTransaccion.COMPLETADA)
                        .createdAt(OffsetDateTime.now())
                        .createdBy(usuario)
                        .build();

        Transaccion saved =
                transaccionRepository.save(transaccion);

        return TransferenciaResponseDto.builder()
                .transaccionId(saved.getId())
                .cuentaOrigenId(saved.getCuentaOrigenId())
                .cuentaDestinoId(saved.getCuentaDestinoId())
                .monto(saved.getMonto())
                .referencia(saved.getReferencia())
                .estado(saved.getEstado().name())
                .build();
    }

}