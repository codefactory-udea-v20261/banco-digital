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
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new ClienteNoEncontradoException(id));

        List<String> camposModificados = new ArrayList<>();
        Cliente actualizado = aplicarCambios(cliente, request, camposModificados, id);

        if (camposModificados.isEmpty()) {
            return mapper.toResponseDto(cliente);
        }

        Cliente guardado = repository.save(actualizado);
        eventPublisher.publish(ClienteActualizadoEvent.of(guardado.getId(), camposModificados));
        return mapper.toResponseDto(guardado);
    }

    private Cliente aplicarCambios(Cliente cliente, ActualizarClienteRequestDto request,
            List<String> camposModificados, UUID id) {
        Cliente actualizado = aplicarCambiosNombre(cliente, request, camposModificados);
        actualizado = aplicarCambiosContacto(actualizado, request, camposModificados, id);
        return aplicarCambioEstado(actualizado, request, camposModificados);
    }

    private Cliente aplicarCambiosNombre(Cliente actualizado, ActualizarClienteRequestDto request,
            List<String> campos) {
        if (cambio(request.getPrimerNombre(), actualizado.getPrimerNombre())) {
            actualizado = actualizado.withPrimerNombre(request.getPrimerNombre());
            campos.add("primerNombre");
        }
        if (cambio(request.getSegundoNombre(), actualizado.getSegundoNombre())) {
            actualizado = actualizado.withSegundoNombre(request.getSegundoNombre());
            campos.add("segundoNombre");
        }
        if (cambio(request.getPrimerApellido(), actualizado.getPrimerApellido())) {
            actualizado = actualizado.withPrimerApellido(request.getPrimerApellido());
            campos.add("primerApellido");
        }
        if (cambio(request.getSegundoApellido(), actualizado.getSegundoApellido())) {
            actualizado = actualizado.withSegundoApellido(request.getSegundoApellido());
            campos.add("segundoApellido");
        }
        return actualizado;
    }

    private Cliente aplicarCambiosContacto(Cliente actualizado, ActualizarClienteRequestDto request,
            List<String> campos, UUID id) {
        if (cambio(request.getEmail(), actualizado.getEmail().valor())) {
            validarEmailUnico(request.getEmail(), id);
            actualizado = actualizado.withEmail(Email.of(request.getEmail()));
            campos.add("email");
        }
        if (cambio(request.getTelefono(), actualizado.getTelefono())) {
            actualizado = actualizado.withTelefono(request.getTelefono());
            campos.add("telefono");
        }
        return actualizado;
    }

    private Cliente aplicarCambioEstado(Cliente actualizado, ActualizarClienteRequestDto request,
            List<String> campos) {
        if (request.getActivo() != null && !Objects.equals(request.getActivo(), actualizado.isActivo())) {
            actualizado = actualizado.withActivo(request.getActivo());
            campos.add("activo");
        }
        return actualizado;
    }

    private void validarEmailUnico(String email, UUID id) {
        if (repository.existsByEmailAndIdNot(email, id)) {
            throw new ClienteYaExisteException("email", email);
        }
    }

    private boolean cambio(String nuevoValor, String valorActual) {
        return nuevoValor != null && !Objects.equals(nuevoValor, valorActual);
    }
}