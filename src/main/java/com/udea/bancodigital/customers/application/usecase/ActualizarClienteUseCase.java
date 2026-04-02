package com.udea.bancodigital.customers.application.usecase;

import com.udea.bancodigital.customers.application.dto.ActualizarClienteRequestDto;
import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.application.mapper.ClienteMapper;
import com.udea.bancodigital.customers.domain.event.ClienteActualizadoEvent;
import com.udea.bancodigital.customers.domain.exception.ClienteNoEncontradoException;
import com.udea.bancodigital.customers.domain.exception.ClienteYaExisteException;
import com.udea.bancodigital.customers.domain.model.Cliente;
import com.udea.bancodigital.customers.domain.model.Email;
import com.udea.bancodigital.customers.domain.port.in.ActualizarClientePort;
import com.udea.bancodigital.customers.domain.port.out.ClienteRepositoryPort;
import com.udea.bancodigital.customers.domain.port.out.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActualizarClienteUseCase implements ActualizarClientePort {

    private final ClienteRepositoryPort repository;
    private final ClienteMapper mapper;
    private final DomainEventPublisher eventPublisher;

    @Override
    @Transactional
    public ClienteResponseDto actualizarCliente(UUID id, ActualizarClienteRequestDto request) {

        // ── 1. Buscar cliente existente ─────────────────────────────────────
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException(id));

        Cliente actualizado = cliente;
        List<String> camposModificados = new ArrayList<>();
        
        if (request.getPrimerNombre() != null && !Objects.equals(request.getPrimerNombre(), actualizado.getPrimerNombre())) {
            actualizado = actualizado.withPrimerNombre(request.getPrimerNombre());
            camposModificados.add("primerNombre");
        }
        if (request.getSegundoNombre() != null && !Objects.equals(request.getSegundoNombre(), actualizado.getSegundoNombre())) {
            actualizado = actualizado.withSegundoNombre(request.getSegundoNombre());
            camposModificados.add("segundoNombre");
        }
        if (request.getPrimerApellido() != null && !Objects.equals(request.getPrimerApellido(), actualizado.getPrimerApellido())) {
            actualizado = actualizado.withPrimerApellido(request.getPrimerApellido());
            camposModificados.add("primerApellido");
        }
        if (request.getSegundoApellido() != null && !Objects.equals(request.getSegundoApellido(), actualizado.getSegundoApellido())) {
            actualizado = actualizado.withSegundoApellido(request.getSegundoApellido());
            camposModificados.add("segundoApellido");
        }
        if (request.getEmail() != null && !Objects.equals(request.getEmail(), actualizado.getEmail().valor())) {
            if (repository.existsByEmailAndIdNot(request.getEmail(), id)) {
                throw new ClienteYaExisteException("email", request.getEmail());
            }
            actualizado = actualizado.withEmail(Email.of(request.getEmail()));
            camposModificados.add("email");
        }
        if (request.getTelefono() != null && !Objects.equals(request.getTelefono(), actualizado.getTelefono())) {
            actualizado = actualizado.withTelefono(request.getTelefono());
            camposModificados.add("telefono");
        }
        if (request.getActivo() != null && !Objects.equals(request.getActivo(), actualizado.isActivo())) {
            actualizado = actualizado.withActivo(request.getActivo());
            camposModificados.add("activo");
        }

        if (camposModificados.isEmpty()) {
            return mapper.toResponseDto(cliente);
        }

        Cliente guardado = repository.save(actualizado);
        eventPublisher.publish(ClienteActualizadoEvent.of(guardado.getId(), camposModificados));

        return mapper.toResponseDto(guardado);
    }
}
