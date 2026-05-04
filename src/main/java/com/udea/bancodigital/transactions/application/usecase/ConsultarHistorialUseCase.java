package com.udea.bancodigital.transactions.application.usecase;

import com.udea.bancodigital.transactions.application.dto.HistorialTransaccionDto;
import com.udea.bancodigital.transactions.application.mapper.HistorialTransaccionMapper;
import com.udea.bancodigital.transactions.domain.port.out.TransaccionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultarHistorialUseCase {
    private final TransaccionRepositoryPort transaccionRepository;
    private final HistorialTransaccionMapper mapper;

    public List<HistorialTransaccionDto> ejecutar(UUID cuentaId) {

        return transaccionRepository
                .findByCuentaIdOrderByFechaDesc(cuentaId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
