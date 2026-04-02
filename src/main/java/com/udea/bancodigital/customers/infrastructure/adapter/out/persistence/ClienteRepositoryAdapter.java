package com.udea.bancodigital.customers.infrastructure.adapter.out.persistence;

import com.udea.bancodigital.customers.application.mapper.ClienteMapper;
import com.udea.bancodigital.customers.domain.model.Cliente;
import com.udea.bancodigital.customers.domain.port.out.ClienteRepositoryPort;
import com.udea.bancodigital.customers.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import com.udea.bancodigital.customers.infrastructure.adapter.out.persistence.repository.ClienteSpringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteSpringRepository repository;
    private final ClienteMapper mapper;

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByCedula(String numeroCedula) {
        return repository.existsByNumeroCedula(numeroCedula);
    }

    @Override
    public Cliente save(Cliente cliente) {
        ClienteEntity entity = mapper.toEntity(cliente);
        ClienteEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Cliente> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }
}
