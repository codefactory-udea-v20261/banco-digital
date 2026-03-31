package com.udea.bancodigital.accounts.domain.port.in;

import com.udea.bancodigital.accounts.application.dto.CrearCuentaRequestDto;
import com.udea.bancodigital.accounts.domain.model.Cuenta;

/**
 * Puerto de Entrada (Input Port) para la creación de cuentas financieras.
 * * REGLA DE NEGOCIO: Este puerto es el punto de entrada para el
 * flujo liderado, el cual requiere la validación obligatoria
 * de la existencia del cliente en el módulo de Customers antes de proceder.
 */
public interface CrearCuentaPort {

    /**
     * Ejecuta el caso de uso para crear una nueva cuenta.
     * @param request DTO con el ID del cliente y el tipo de cuenta.
     * @return Objeto de dominio Cuenta con ID y número de cuenta generados.
     * @throws RuntimeException si el cliente no existe o los datos son inválidos.
     */
    Cuenta crearCuenta(CrearCuentaRequestDto request);

}

