package com.udea.bancodigital.transactions.domain.port.in;

import com.udea.bancodigital.transactions.application.dto.TransferenciaRequestDto;
import com.udea.bancodigital.transactions.application.dto.TransferenciaResponseDto;

public interface TransferirDineroPort {

    TransferenciaResponseDto transferir(
            TransferenciaRequestDto request,
            String usuario
    );

}