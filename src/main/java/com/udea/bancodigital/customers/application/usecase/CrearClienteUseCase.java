package com.udea.bancodigital.customers.application.usecase;

import com.udea.bancodigital.customers.application.dto.CrearClienteRequestDto;
import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.application.mapper.ClienteMapper;
import com.udea.bancodigital.customers.domain.event.ClienteRegistradoEvent;
import com.udea.bancodigital.customers.domain.exception.ClienteYaExisteException;
import com.udea.bancodigital.customers.domain.model.Cliente;
import com.udea.bancodigital.customers.domain.port.out.ClienteRepositoryPort;
import com.udea.bancodigital.customers.domain.port.out.DomainEventPublisher;
import com.udea.bancodigital.customers.domain.port.in.CrearClientePort;
import com.udea.bancodigital.shared.util.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrearClienteUseCase
        implements CrearClientePort, UseCase<CrearClienteRequestDto, ClienteResponseDto> {

    // Puertos de salida — las implementaciones reales están en infrastructure/
    private final ClienteRepositoryPort clienteRepository;
    private final ClienteMapper clienteMapper;
    private final DomainEventPublisher eventPublisher;

    @Override
    public ClienteResponseDto crearCliente(CrearClienteRequestDto request) {
        return ejecutar(request);
    }

    @Override
    @Transactional
    public ClienteResponseDto ejecutar(CrearClienteRequestDto request) {
        // ── 1. Validar unicidad ───────────────────────────────────────────────
        if (clienteRepository.existsByEmail(request.getEmail())) {
            throw new ClienteYaExisteException(request.getEmail());
        }
        if (clienteRepository.existsByCedula(request.getNumeroCedula())) {
            throw new ClienteYaExisteException("cédula", request.getNumeroCedula());
        }

        // ── 2. Construir entidad de dominio ──────────────────────────────────
        Cliente cliente = clienteMapper.toDomain(request);

        // ── 3. Persistir ─────────────────────────────────────────────────────
        Cliente clienteGuardado = clienteRepository.save(cliente);

        // ── 4. Publicar evento de dominio ────────────────────────────────────
        // Otros módulos pueden reaccionar: crear cuenta inicial, enviar email, etc.
        String nombreCompleto = String.format("%s %s", 
                                              clienteGuardado.getPrimerNombre(), 
                                              clienteGuardado.getPrimerApellido());
        
        eventPublisher.publish(
            ClienteRegistradoEvent.of(
                clienteGuardado.getId(),
                clienteGuardado.getEmail().valor(),
                nombreCompleto
            )
        );

        // ── 5. Retornar respuesta ────────────────────────────────────────────
        return clienteMapper.toResponseDto(clienteGuardado);
    }
}
