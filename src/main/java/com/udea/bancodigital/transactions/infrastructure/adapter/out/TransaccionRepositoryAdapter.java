package com.udea.bancodigital.transactions.infrastructure.adapter.out;

import com.udea.bancodigital.transactions.domain.model.Transaccion;
import com.udea.bancodigital.transactions.domain.port.out.TransaccionRepositoryPort;
import com.udea.bancodigital.transactions.infrastructure.entity.TransaccionEntity;
import com.udea.bancodigital.transactions.infrastructure.mapper.TransaccionMapper;
import com.udea.bancodigital.transactions.infrastructure.repository.TransaccionJpaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransaccionRepositoryAdapter
        implements TransaccionRepositoryPort {

    private final TransaccionJpaRepository repository;
    private final TransaccionMapper mapper;

    @Override
    public Transaccion save(
            Transaccion transaccion) {

        TransaccionEntity entity =
                mapper.toEntity(transaccion);

        TransaccionEntity saved =
                repository.save(entity);

        return mapper.toDomain(saved);
    }

}