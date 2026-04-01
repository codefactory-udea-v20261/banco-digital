package com.udea.bancodigital.customers.application.usecase;

import com.udea.bancodigital.customers.application.dto.ActualizarClienteRequestDto;
import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.application.mapper.ClienteMapper;
import com.udea.bancodigital.customers.domain.exception.ClienteNoEncontradoException;
import com.udea.bancodigital.customers.domain.model.Cliente;
import com.udea.bancodigital.customers.domain.model.Email;
import com.udea.bancodigital.customers.domain.port.in.ActualizarClientePort;
import com.udea.bancodigital.customers.domain.port.out.ClienteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActualizarClienteUseCase implements ActualizarClientePort {

    private final ClienteRepositoryPort repository;
    private final ClienteMapper mapper;

    @Override
    @Transactional
    public ClienteResponseDto actualizarCliente(UUID id, ActualizarClienteRequestDto request) {

        // ── 1. Buscar cliente existente ─────────────────────────────────────
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException(id));

        // ── 2. Actualizar solo campos NO nulos (PATCH semántica) ───────────
        // Usamos @With para crear una nueva instancia inmutable con los cambios
        Cliente actualizado = cliente;
        
        if (request.getPrimerNombre() != null) {
            actualizado = actualizado.withPrimerNombre(request.getPrimerNombre());
        }
        if (request.getSegundoNombre() != null) {
            actualizado = actualizado.withSegundoNombre(request.getSegundoNombre());
        }
        if (request.getPrimerApellido() != null) {
            actualizado = actualizado.withPrimerApellido(request.getPrimerApellido());
        }
        if (request.getSegundoApellido() != null) {
            actualizado = actualizado.withSegundoApellido(request.getSegundoApellido());
        }
        if (request.getEmail() != null) {
            actualizado = actualizado.withEmail(Email.of(request.getEmail()));
        }
        if (request.getTelefono() != null) {
            actualizado = actualizado.withTelefono(request.getTelefono());
        }
        if (request.getActivo() != null) {
            actualizado = actualizado.withActivo(request.getActivo());
        }

        // ── 3. Persistir cambios ────────────────────────────────────────────
        Cliente guardado = repository.save(actualizado);

        // ── 4. Retornar respuesta ───────────────────────────────────────────
        return mapper.toResponseDto(guardado);
    }
}
