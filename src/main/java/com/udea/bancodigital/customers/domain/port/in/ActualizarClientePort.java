package com.udea.bancodigital.customers.domain.port.in;


import com.udea.bancodigital.customers.application.dto.ActualizarClienteRequestDto;
import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;

import java.util.UUID;

public interface ActualizarClientePort {

    ClienteResponseDto actualizarCliente(UUID id, ActualizarClienteRequestDto request);
}