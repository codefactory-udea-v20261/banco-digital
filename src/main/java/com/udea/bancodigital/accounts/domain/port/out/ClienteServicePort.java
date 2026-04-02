package com.udea.bancodigital.accounts.domain.port.out;

import java.util.UUID;

/**
 * Puerto de salida para interactuar con el módulo de clientes.
 * 
 * RESPONSABILIDAD:
 * - Verificar existencia de clientes
 * - Validar que un cliente existe antes de crear una cuenta
 * 
 * NOTA: Este puerto permite comunicación entre módulos (accounts → customers)
 * siguiendo Clean Architecture. El módulo accounts NO conoce la implementación
 * de customers, solo la interfaz.
 */
public interface ClienteServicePort {
    
    /**
     * Verifica si existe un cliente con el ID dado.
     *
     * @param clienteId ID del cliente a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existeCliente(UUID clienteId);
    
    /**
     * Verifica si un cliente está activo.
     *
     * @param clienteId ID del cliente
     * @return true si está activo, false si está inactivo o no existe
     */
    boolean isClienteActivo(UUID clienteId);
}

