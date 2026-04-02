package com.udea.bancodigital.customers.domain.port.in;

import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;

import java.util.UUID;

public interface ObtenerClientePort {
    ClienteResponseDto obtenerPorId(UUID id);
}
