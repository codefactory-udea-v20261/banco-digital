package com.udea.bancodigital.transactions.domain.port.out;

import com.udea.bancodigital.transactions.domain.model.Transaccion;


public interface TransaccionRepositoryPort {

    Transaccion save(Transaccion transaccion);

}
