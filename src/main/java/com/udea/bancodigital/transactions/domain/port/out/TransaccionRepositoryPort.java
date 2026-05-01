package com.udea.bancodigital.transactions.domain.port.out;

import com.udea.bancodigital.transactions.domain.model.Transaccion;

import java.util.List;
import java.util.UUID;

public interface TransaccionRepositoryPort {

    Transaccion save(Transaccion transaccion);
    List<Transaccion> findByCuentaIdOrderByFechaDesc(UUID cuentaId);

}
