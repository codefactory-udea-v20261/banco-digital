package com.udea.bancodigital.customers.domain.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * La cédula es un campo inmutable. Se lanza si se intenta modificar.
 * Representa una regla de negocio del sistema.
 */
public class CampoCedularInmutableException extends BusinessException {

    public CampoCedularInmutableException() {
        super("CAMPO_INMUTABLE",
              "El número de cédula no puede ser modificado una vez registrado",
              HttpStatus.BAD_REQUEST);
    }
}
