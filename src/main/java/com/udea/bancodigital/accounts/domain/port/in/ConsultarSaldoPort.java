package com.udea.bancodigital.accounts.domain.port.in;

import java.util.UUID;
import com.udea.bancodigital.accounts.application.dto.ConsultarSaldoResponseDto;

public interface ConsultarSaldoPort {
    ConsultarSaldoResponseDto consultarSaldo(UUID cuentaId, UUID clienteId);
}
