package com.udea.bancodigital.customers.application.usecase;

import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.application.mapper.ClienteMapper;
import com.udea.bancodigital.customers.domain.exception.ClienteNoEncontradoException;
import com.udea.bancodigital.customers.domain.model.Cliente;
import com.udea.bancodigital.customers.domain.port.in.ObtenerClientePort;
import com.udea.bancodigital.customers.domain.port.out.ClienteAccessControlPort;
import com.udea.bancodigital.customers.domain.port.out.ClienteRepositoryPort;
import com.udea.bancodigital.shared.util.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ObtenerClienteUseCase implements ObtenerClientePort, UseCase<UUID, ClienteResponseDto> {
    private final ClienteRepositoryPort clienteRepository;
    private final ClienteMapper clienteMapper;
    private final ClienteAccessControlPort accessControl;

    @Override
    public ClienteResponseDto obtenerPorId(UUID id) {
        return ejecutar(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDto ejecutar(UUID id) {
        
        // ── 0. Validar Acceso ──────────────────────────────────────────────
        accessControl.validateCanView(id);

        // ── 1. Buscar cliente ───────────────────────────────────────────────
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException(id));

        // ── 2. Mapear a DTO ────────────────────────────────────────────────
        return clienteMapper.toResponseDto(cliente);
    }
}
