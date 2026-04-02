package com.udea.bancodigital.customers.infrastructure.adapter.in.event;

import com.udea.bancodigital.customers.domain.event.ClienteActualizadoEvent;
import com.udea.bancodigital.customers.domain.event.ClienteRegistradoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Ejemplo de listener de eventos de dominio.
 * 
 * ESTE ES UN EJEMPLO para mostrar cómo otros módulos pueden reaccionar
 * a eventos del módulo customers. En producción, este listener estaría
 * en el módulo que necesite reaccionar (ej: notifications, accounts, etc.)
 * 
 * USO:
 * - @TransactionalEventListener asegura que el evento se procese después del commit
 * - @Async permite procesamiento asíncrono sin bloquear el request
 * - Otros módulos pueden tener sus propios listeners sin modificar customers
 * 
 * EJEMPLOS REALES:
 * - NotificationsModule → Enviar email de bienvenida
 * - AccountsModule → Crear cuenta inicial automáticamente
 * - AnalyticsModule → Actualizar métricas de clientes
 * - AuditModule → Registrar auditoría de registro
 */
@Component
@Slf4j
public class ClienteEventListener {
    
    /**
     * Reacciona al evento ClienteRegistradoEvent DESPUÉS del commit de BD.
     * 
     * TransactionPhase.AFTER_COMMIT garantiza que el cliente ya está persistido.
     * Si este handler falla, no afecta la transacción principal.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onClienteRegistrado(ClienteRegistradoEvent event) {
        log.info("EVENTO RECIBIDO: Cliente Registrado");
        log.info("Cliente ID: {}", event.clienteId());
        log.info("Email: {}", event.email());
        log.info("Nombre: {}", event.nombreCompleto());
        log.info("Ocurrió: {}", event.occurredOn());

        // AQUÍ otros módulos harían su trabajo:
        // - notificationService.enviarEmailBienvenida(event.email(), event.nombreCompleto());
        // - accountService.crearCuentaInicial(event.clienteId());
        // - analyticsService.registrarNuevoCliente();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onClienteActualizado(ClienteActualizadoEvent event) {
        log.info("EVENTO RECIBIDO: Cliente Actualizado");
        log.info("Cliente ID: {}", event.clienteId());
        log.info("Campos modificados: {}", event.camposModificados());
        log.info("Ocurrió: {}", event.occurredOn());
    }
}
