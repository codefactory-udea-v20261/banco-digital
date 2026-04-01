package com.udea.bancodigital.customers.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Interfaz base para todos los eventos de dominio.
 * 
 * Los eventos de dominio representan algo que ha ocurrido en el dominio
 * y que podría ser de interés para otras partes del sistema (otros módulos).
 * 
 * ARQUITECTURA:
 * - Los eventos viven en la capa de dominio (son conceptos de negocio)
 * - Son inmutables (se crea un evento y no se modifica)
 * - Usan records de Java para inmutabilidad por defecto
 */
public interface DomainEvent {
    
    /**
     * Identificador único del evento.
     */
    UUID eventId();
    
    /**
     * Timestamp de cuando ocurrió el evento.
     */
    Instant occurredOn();
    
    /**
     * Tipo de evento para clasificación/routing.
     */
    String eventType();
}
