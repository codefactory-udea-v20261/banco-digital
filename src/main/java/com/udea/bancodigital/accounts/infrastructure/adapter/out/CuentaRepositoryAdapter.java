package com.udea.bancodigital.accounts.infrastructure.adapter.out;

import com.udea.bancodigital.accounts.domain.model.Cuenta;
import com.udea.bancodigital.accounts.domain.port.out.CuentaRepositoryPort;
import com.udea.bancodigital.accounts.infrastructure.entity.CuentaEntity;
import com.udea.bancodigital.accounts.infrastructure.mapper.CuentaEntityMapper;
import com.udea.bancodigital.accounts.infrastructure.repository.CuentaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador JPA para el repositorio de cuentas.
 * 
 * Implementa el puerto del dominio usando Spring Data JPA.
 * Realiza la conversión entre entidades JPA y modelos de dominio.
 */
@Component
@RequiredArgsConstructor
public class CuentaRepositoryAdapter implements CuentaRepositoryPort {
    
    private final CuentaJpaRepository jpaRepository;
    private final CuentaEntityMapper mapper;
    
    @Override
    public Cuenta save(Cuenta cuenta) {
        CuentaEntity entity = mapper.toEntity(cuenta);
        CuentaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<Cuenta> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<Cuenta> findByNumeroCuenta(String numeroCuenta) {
        return jpaRepository.findByNumeroCuenta(numeroCuenta)
                .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsByNumeroCuenta(String numeroCuenta) {
        return jpaRepository.existsByNumeroCuenta(numeroCuenta);
    }
}
