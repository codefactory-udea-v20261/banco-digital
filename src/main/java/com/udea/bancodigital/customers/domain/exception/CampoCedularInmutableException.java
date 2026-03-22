package com.udea.bancodigital.customers.domain.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * HU3 — La cédula es un campo inmutable. Se lanza si alguien intenta modificarla via PATCH.
 * Regla de negocio documentada en ADR-001 y CODING_STANDARDS.
 */
public class CampoCedularInmutableException extends BusinessException {

    public CampoCedularInmutableException() {
        super("CAMPO_INMUTABLE",
              "El número de cédula no puede ser modificado una vez registrado",
              HttpStatus.BAD_REQUEST);
    }
}
