package com.udea.bancodigital.customers.domain.port.out;

import java.util.UUID;

public interface ClienteAccessControlPort {
    void validateCanView(UUID clienteId);

    boolean canManageClientes();
}
