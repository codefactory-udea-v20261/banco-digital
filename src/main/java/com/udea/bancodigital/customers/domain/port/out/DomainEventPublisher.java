package com.udea.bancodigital.customers.domain.port.out;

import com.udea.bancodigital.customers.domain.event.DomainEvent;

/**
 * Puerto de salida para publicar eventos de dominio.
 * 
 * ARQUITECTURA HEXAGONAL:
 * - Este puerto vive en el dominio (abstracción)
 * - La implementación concreta está en infrastructure
 * - Permite cambiar el mecanismo de eventos sin afectar el dominio
 * 
 * IMPLEMENTACIONES POSIBLES:
 * - Spring ApplicationEventPublisher (in-process, síncrono)
 * - Kafka/RabbitMQ (asíncrono, entre servicios)
 * - Base de datos (Event Sourcing)
 * 
 * PRINCIPIOS:
 * - Dependency Inversion: UseCase depende de la abstracción
 * - Open/Closed: Nuevas implementaciones sin modificar UseCases
 */
public interface DomainEventPublisher {
    
    /**
     * Publica un evento de dominio.
     * 
     * @param event El evento a publicar
     * @param <T> Tipo del evento (debe implementar DomainEvent)
     */
    <T extends DomainEvent> void publish(T event);
}
