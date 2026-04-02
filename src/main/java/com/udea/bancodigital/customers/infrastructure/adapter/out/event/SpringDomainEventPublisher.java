package com.udea.bancodigital.customers.infrastructure.adapter.out.event;

import com.udea.bancodigital.customers.domain.event.DomainEvent;
import com.udea.bancodigital.customers.domain.port.out.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida para publicar eventos de dominio.
 * 
 * IMPLEMENTACIÓN:
 * - Usa Spring ApplicationEventPublisher (eventos síncronos in-process)
 * - Otros módulos pueden escuchar con @EventListener o @TransactionalEventListener
 * - Los eventos se publican dentro de la misma transacción
 * 
 * EVOLUCIÓN FUTURA:
 * - Se puede cambiar a Kafka/RabbitMQ sin modificar los UseCases
 * - Se puede agregar persistencia de eventos (Event Store)
 * - Se puede agregar retry logic o dead letter queue
 * 
 * PRINCIPIOS:
 * - Adapter Pattern: Adapta Spring events al puerto del dominio
 * - Dependency Inversion: Implementa la interfaz del dominio
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SpringDomainEventPublisher implements DomainEventPublisher {
    
    private final ApplicationEventPublisher eventPublisher;
    
    @Override
    public <T extends DomainEvent> void publish(T event) {
        log.debug("Publishing domain event: {} with ID: {}", 
                  event.eventType(), 
                  event.eventId());
        
        eventPublisher.publishEvent(event);
        
        log.info("Domain event published successfully: {} - Cliente: {}", 
                 event.eventType(),
                 event.eventId());
    }
}
