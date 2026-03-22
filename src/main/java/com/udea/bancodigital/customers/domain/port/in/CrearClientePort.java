package com.udea.bancodigital.customers.domain.port.in;

import com.udea.bancodigital.customers.application.dto.CrearClienteRequestDto;
import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;

/**
 * Puerto de entrada (Input Port) para el caso de uso HU1.
 * El controlador REST lo usa vía inyección — nunca llama al UseCase directamente.
 * Esto permite cambiar la implementación sin tocar el controlador.
 */
public interface CrearClientePort {
    ClienteResponseDto crearCliente(CrearClienteRequestDto request);
}
