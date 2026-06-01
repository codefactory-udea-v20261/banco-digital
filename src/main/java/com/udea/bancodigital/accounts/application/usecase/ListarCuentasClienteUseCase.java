package com.udea.bancodigital.accounts.application.usecase;

import com.udea.bancodigital.accounts.application.dto.CuentaResponseDto;
import com.udea.bancodigital.accounts.application.mapper.CuentaMapper;
import com.udea.bancodigital.accounts.domain.exception.ClienteNoEncontradoException;
import com.udea.bancodigital.accounts.domain.port.in.ListarCuentasClientePort;
import com.udea.bancodigital.accounts.domain.port.out.ClienteServicePort;
import com.udea.bancodigital.accounts.domain.port.out.CuentaRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ListarCuentasClienteUseCase implements ListarCuentasClientePort {

    private final CuentaRepositoryPort cuentaRepository;
    private final ClienteServicePort clienteService;
    private final CuentaMapper cuentaMapper;

    @Override
    public List<CuentaResponseDto> listarPorClienteId(UUID clienteId) {
        if (!clienteService.existeCliente(clienteId)) {
            throw new ClienteNoEncontradoException("No se encontró un cliente con ID: " + clienteId);
        }

        return cuentaRepository.findAllByClienteId(clienteId).stream()
                .map(cuentaMapper::toResponseDto)
                .toList();
    }
}
