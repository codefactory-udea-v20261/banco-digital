package com.udea.bancodigital.customers.domain.port.out;

import java.util.UUID;

/**
 * Puerto de salida para habilitar el acceso digital del cliente recién registrado.
 *
 * La implementación concreta vive en el módulo auth para evitar que customers
 * dependa directamente de detalles de autenticación.
 */
public interface ClienteAccessProvisioningPort {

    boolean existsByEmail(String email);

    void provisionAccess(UUID clienteId, String email);
}
