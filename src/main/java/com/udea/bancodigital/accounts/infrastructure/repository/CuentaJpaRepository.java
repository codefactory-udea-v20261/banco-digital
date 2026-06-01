package com.udea.bancodigital.accounts.infrastructure.repository;

import com.udea.bancodigital.accounts.infrastructure.entity.CuentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para CuentaEntity.
 */
@Repository
public interface CuentaJpaRepository extends JpaRepository<CuentaEntity, UUID> {

    Optional<CuentaEntity> findByNumeroCuenta(String numeroCuenta);

    boolean existsByNumeroCuenta(String numeroCuenta);

    List<CuentaEntity> findByClienteIdOrderByFechaAperturaDesc(UUID clienteId);
}
