package com.udea.bancodigital.customers.domain.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Evento de dominio: Un cliente existente ha actualizado su información.
 * 
 * CASOS DE USO:
 * - Auditoría de cambios de datos personales
 * - Notificar al cliente sobre cambios en su perfil
 * - Sincronizar información con sistemas externos
 * - Validar cambios de email (puede requerir verificación)
 * 
 * @param eventId Identificador único del evento
 * @param clienteId ID del cliente actualizado
 * @param camposModificados Lista de nombres de campos que cambiaron
 * @param occurredOn Timestamp de la actualización
 */
public record ClienteActualizadoEvent(
        UUID eventId,
        UUID clienteId,
        List<String> camposModificados,
        Instant occurredOn
) implements DomainEvent {
    
    /**
     * Factory method para crear el evento.
     */
    public static ClienteActualizadoEvent of(UUID clienteId, List<String> camposModificados) {
        return new ClienteActualizadoEvent(
                UUID.randomUUID(),
                clienteId,
                camposModificados,
                Instant.now()
        );
    }
    
    @Override
    public String eventType() {
        return "customers.cliente.actualizado";
    }
}
