package com.udea.bancodigital.customers.domain.port.out;

import com.udea.bancodigital.customers.domain.model.Cliente;

import java.util.Optional;
import java.util.UUID;

public interface ClienteRepositoryPort {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, UUID id);
    boolean existsByCedula(String numeroCedula);
    Cliente save(Cliente cliente);
    Optional<Cliente> findById(UUID id);
}
