package com.udea.bancodigital.accounts.domain.port.out;

import com.udea.bancodigital.accounts.domain.model.Cuenta;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para acceso a repositorio de cuentas.
 * 
 * RESPONSABILIDAD:
 * - Persistir cuentas
 * - Buscar cuentas por diferentes criterios
 * - Verificar existencia de cuentas
 */
public interface CuentaRepositoryPort {
    
    /**
     * Guarda una cuenta (nueva o actualizada).
     *
     * @param cuenta Cuenta a guardar
     * @return Cuenta guardada
     */
    Cuenta save(Cuenta cuenta);
    
    /**
     * Busca una cuenta por su ID.
     *
     * @param id ID de la cuenta
     * @return Optional con la cuenta si existe
     */
    Optional<Cuenta> findById(UUID id);
    
    /**
     * Busca una cuenta por su número de cuenta.
     *
     * @param numeroCuenta Número de cuenta
     * @return Optional con la cuenta si existe
     */
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
    
    /**
     * Verifica si existe una cuenta con el número dado.
     *
     * @param numeroCuenta Número de cuenta
     * @return true si existe, false en caso contrario
     */
    boolean existsByNumeroCuenta(String numeroCuenta);

    /**
     * Obtiene todas las cuentas asociadas a un cliente.
     *
     * @param clienteId ID del cliente
     * @return Lista de cuentas (vacía si el cliente no tiene cuentas)
     */
    List<Cuenta> findAllByClienteId(UUID clienteId);
}
