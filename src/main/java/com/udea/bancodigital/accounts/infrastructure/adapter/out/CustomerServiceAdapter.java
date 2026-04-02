package com.udea.bancodigital.accounts.infrastructure.adapter.out;

import com.udea.bancodigital.accounts.domain.port.out.ClienteServicePort;
import com.udea.bancodigital.customers.domain.port.out.ClienteRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adaptador para comunicación entre módulos: accounts → customers.
 * 
 * Este adaptador permite que el módulo accounts verifique la existencia
 * de clientes sin depender directamente del módulo customers.
 * 
 * ARQUITECTURA MODULAR:
 * - Accounts NO conoce la implementación de Customers
 * - Solo depende del puerto ClienteRepositoryPort
 * - Permite evolución independiente de los módulos
 * 
 * PRINCIPIOS:
 * - Indirection (GRASP): Introduce un nivel de indirección
 * - Protected Variations: Protege accounts de cambios en customers
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceAdapter implements ClienteServicePort {
    
    private final ClienteRepositoryPort clienteRepository;
    
    @Override
    public boolean existeCliente(UUID clienteId) {
        boolean exists = clienteRepository.findById(clienteId).isPresent();
        log.debug("Verificación de existencia de cliente {}: {}", clienteId, exists);
        return exists;
    }
    
    @Override
    public boolean isClienteActivo(UUID clienteId) {
        return clienteRepository.findById(clienteId)
                .map(cliente -> cliente.isActivo())
                .orElse(false);
    }
}
