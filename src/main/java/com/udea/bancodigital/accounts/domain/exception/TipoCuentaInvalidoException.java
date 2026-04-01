package com.udea.bancodigital.accounts.domain.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class TipoCuentaInvalidoException extends BusinessException {

    public TipoCuentaInvalidoException(String tipoCuenta) {
        super(
                "TIPO_CUENTA_INVALIDO",
                "El tipo de cuenta '" + tipoCuenta + "' no es válido. Valores permitidos: AHORRO, CORRIENTE.",
                HttpStatus.BAD_REQUEST
        );
    }
}
