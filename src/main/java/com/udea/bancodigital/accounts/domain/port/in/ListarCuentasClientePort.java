package com.udea.bancodigital.accounts.domain.port.in;

import com.udea.bancodigital.accounts.application.dto.CuentaResponseDto;

import java.util.List;
import java.util.UUID;

public interface ListarCuentasClientePort {

    List<CuentaResponseDto> listarPorClienteId(UUID clienteId);
}
