package com.udea.bancodigital.transactions.application.usecase;

import com.udea.bancodigital.transactions.application.dto.RetiroRequestDto;
import com.udea.bancodigital.transactions.domain.enums.EstadoTransaccion;
import com.udea.bancodigital.transactions.domain.enums.TipoTransaccion;
import com.udea.bancodigital.transactions.domain.exception.CuentaTransaccionException;
import com.udea.bancodigital.transactions.domain.exception.SaldoInsuficienteException;
import com.udea.bancodigital.transactions.domain.model.Transaccion;
import com.udea.bancodigital.transactions.domain.port.out.CuentaServicePort;
import com.udea.bancodigital.transactions.domain.port.out.TransaccionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RealizarRetiroUseCase {
    private final TransaccionRepositoryPort transaccionRepository;
    private final CuentaServicePort cuentaService;

    @Transactional
    public Transaccion ejecutar(RetiroRequestDto request) {

        // 1. Validar existencia y obtener saldo
        BigDecimal saldoActual = cuentaService.consultarSaldo(request.getCuentaId())
                .orElseThrow(() -> CuentaTransaccionException.noEncontrada(request.getCuentaId()));

        // 2. Validar saldo suficiente
        if (saldoActual.compareTo(request.getMonto()) < 0) {
            throw new SaldoInsuficienteException(request.getCuentaId(), saldoActual, request.getMonto());
        }

        // 3. Calcular nuevo saldo
        BigDecimal nuevoSaldo = saldoActual.subtract(request.getMonto());

        // 4. Actualizar en el "módulo" de cuentas
        cuentaService.actualizarSaldo(request.getCuentaId(), nuevoSaldo);

        // 5. Registrar transacción
        String referencia = String.format(
                "RET-%013d-%s",
                System.currentTimeMillis(),
                UUID.randomUUID().toString().replace("-", "").substring(0, 12));

        Transaccion retiro = Transaccion.builder()
                .id(UUID.randomUUID())
                .cuentaOrigenId(request.getCuentaId())
                .tipoId(TipoTransaccion.RETIRO.getId())
                .monto(request.getMonto())
                .saldoAnterior(saldoActual)
                .saldoPosterior(nuevoSaldo)
                .estado(EstadoTransaccion.COMPLETADA)
                .descripcion(request.getDescripcion() != null ? request.getDescripcion() : "Retiro de efectivo")
                .referencia(referencia)
                .createdAt(OffsetDateTime.now())
                .createdBy("SYSTEM_SWAGGER")
                .build();

        return transaccionRepository.save(retiro);
    }
}
