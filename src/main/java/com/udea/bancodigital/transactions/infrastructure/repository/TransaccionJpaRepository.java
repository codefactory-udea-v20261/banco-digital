package com.udea.bancodigital.transactions.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import com.udea.bancodigital.transactions.infrastructure.entity.TransaccionEntity;

@Repository
public interface TransaccionJpaRepository extends JpaRepository<TransaccionEntity, UUID> {
    List<TransaccionEntity> findByCuentaOrigenIdOrderByCreatedAtDesc(UUID cuentaId);
}