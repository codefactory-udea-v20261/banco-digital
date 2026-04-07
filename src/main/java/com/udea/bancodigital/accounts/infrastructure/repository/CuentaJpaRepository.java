package com.udea.bancodigital.accounts.infrastructure.repository;

import com.udea.bancodigital.accounts.infrastructure.entity.CuentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para CuentaEntity.
 */
@Repository
public interface CuentaJpaRepository extends JpaRepository<CuentaEntity, UUID> {

    Optional<CuentaEntity> findByNumeroCuenta(String numeroCuenta);

    boolean existsByNumeroCuenta(String numeroCuenta);

    @Query(value = "SELECT obtener_saldo_total_cliente(:clienteId)", nativeQuery = true)
    BigDecimal obtenerSaldoTotalCliente(@Param("clienteId") UUID clienteId);
}
