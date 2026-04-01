package com.udea.bancodigital.customers.shared.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class DatosIncompletosException extends BusinessException {

    public DatosIncompletosException() {
        super(
                "DATOS_INCOMPLETOS",
                "La información proporcionada es insuficiente",
                HttpStatus.BAD_REQUEST
        );
    }
}
