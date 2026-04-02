package com.udea.bancodigital.customers.infrastructure.adapter.out.persistence.repository;

import com.udea.bancodigital.customers.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClienteSpringRepository extends JpaRepository<ClienteEntity, UUID> {
    boolean existsByEmail(String email);
    boolean existsByNumeroCedula(String numeroCedula);
}
