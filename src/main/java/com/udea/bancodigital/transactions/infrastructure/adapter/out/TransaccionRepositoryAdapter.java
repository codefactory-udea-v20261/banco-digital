package com.udea.bancodigital.transactions.infrastructure.adapter.out;

import com.udea.bancodigital.transactions.domain.model.Transaccion;
import com.udea.bancodigital.transactions.domain.port.out.TransaccionRepositoryPort;
import com.udea.bancodigital.transactions.infrastructure.entity.TransaccionEntity;
import com.udea.bancodigital.transactions.infrastructure.mapper.TransaccionMapper;
import com.udea.bancodigital.transactions.infrastructure.repository.TransaccionJpaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;

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

    @Override
    public List<Transaccion> findByCuentaIdOrderByFechaDesc(UUID cuentaId) {

        return repository
                .findByCuentaOrigenIdOrderByCreatedAtDesc(cuentaId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

}