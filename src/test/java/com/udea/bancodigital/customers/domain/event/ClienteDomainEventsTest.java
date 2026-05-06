package com.udea.bancodigital.customers.domain.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Eventos de dominio - Customers")
class ClienteDomainEventsTest {
    @Nested
    @DisplayName("ClienteRegistradoEvent")
    class ClienteRegistradoEventTest {

        @Test
        @DisplayName("of() debe crear evento con todos los campos")
        void ofDebeCrearEventoConTodosLosCampos() {
            UUID clienteId = UUID.randomUUID();
            ClienteRegistradoEvent event = ClienteRegistradoEvent.of(clienteId, "juan@banco.com", "Juan Pérez");

            assertThat(event.eventId()).isNotNull();
            assertThat(event.clienteId()).isEqualTo(clienteId);
            assertThat(event.email()).isEqualTo("juan@banco.com");
            assertThat(event.nombreCompleto()).isEqualTo("Juan Pérez");
            assertThat(event.occurredOn()).isNotNull();
        }

        @Test
        @DisplayName("Cada llamada a of() debe generar un eventId único")
        void cadaLlamadaDebeGenerarEventIdUnico() {
            UUID clienteId = UUID.randomUUID();
            ClienteRegistradoEvent e1 = ClienteRegistradoEvent.of(clienteId, "a@b.com", "Test");
            ClienteRegistradoEvent e2 = ClienteRegistradoEvent.of(clienteId, "a@b.com", "Test");

            assertThat(e1.eventId()).isNotEqualTo(e2.eventId());
        }

        @Test
        @DisplayName("eventType() debe retornar el tipo correcto")
        void eventTypeDebeRetornarTipoCorrecto() {
            ClienteRegistradoEvent event = ClienteRegistradoEvent.of(UUID.randomUUID(), "a@b.com", "Test");
            assertThat(event.eventType()).isEqualTo("customers.cliente.registrado");
        }
    }

    @Nested
    @DisplayName("ClienteActualizadoEvent")
    class ClienteActualizadoEventTest {

        @Test
        @DisplayName("of() debe crear evento con campos modificados")
        void ofDebeCrearEventoConCamposModificados() {
            UUID clienteId = UUID.randomUUID();
            List<String> campos = List.of("email", "telefono");
            ClienteActualizadoEvent event = ClienteActualizadoEvent.of(clienteId, campos);

            assertThat(event.eventId()).isNotNull();
            assertThat(event.clienteId()).isEqualTo(clienteId);
            assertThat(event.camposModificados()).containsExactly("email", "telefono");
            assertThat(event.occurredOn()).isNotNull();
        }

        @Test
        @DisplayName("eventType() debe retornar el tipo correcto")
        void eventTypeDebeRetornarTipoCorrecto() {
            ClienteActualizadoEvent event = ClienteActualizadoEvent.of(UUID.randomUUID(), List.of("email"));
            assertThat(event.eventType()).isEqualTo("customers.cliente.actualizado");
        }
    }

}
