package com.udea.bancodigital.transactions.domain.port.in;

import com.udea.bancodigital.transactions.domain.model.Transaccion;

import java.util.UUID;

public interface TransaccionRepositoryPort {

    Transaccion save(Transaccion transaccion);

}
