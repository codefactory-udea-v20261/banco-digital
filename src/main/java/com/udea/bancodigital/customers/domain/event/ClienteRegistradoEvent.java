package com.udea.bancodigital.customers.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento de dominio: Un nuevo cliente ha sido registrado en el sistema.
 * 
 * CASOS DE USO:
 * - El módulo de cuentas puede crear una cuenta inicial automáticamente
 * - El módulo de notificaciones puede enviar email de bienvenida
 * - El módulo de auditoría puede registrar la acción
 * - El módulo de analytics puede actualizar métricas
 * 
 * Este evento permite comunicación desacoplada entre módulos del monolito.
 * 
 * @param eventId Identificador único del evento
 * @param clienteId ID del cliente que fue registrado
 * @param email Email del cliente (para notificaciones)
 * @param nombreCompleto Nombre completo para personalización
 * @param occurredOn Timestamp del registro
 */
public record ClienteRegistradoEvent(
        UUID eventId,
        UUID clienteId,
        String email,
        String nombreCompleto,
        Instant occurredOn
) implements DomainEvent {
    
    /**
     * Factory method para crear el evento.
     * Genera automáticamente eventId y occurredOn.
     */
    public static ClienteRegistradoEvent of(UUID clienteId, String email, String nombreCompleto) {
        return new ClienteRegistradoEvent(
                UUID.randomUUID(),
                clienteId,
                email,
                nombreCompleto,
                Instant.now()
        );
    }
    
    @Override
    public String eventType() {
        return "customers.cliente.registrado";
    }
}
