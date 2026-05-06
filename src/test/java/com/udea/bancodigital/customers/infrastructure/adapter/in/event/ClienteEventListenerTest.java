package com.udea.bancodigital.customers.infrastructure.adapter.in.event;

import com.udea.bancodigital.customers.domain.event.ClienteActualizadoEvent;
import com.udea.bancodigital.customers.domain.event.ClienteRegistradoEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;

@DisplayName("ClienteEventListener")
class ClienteEventListenerTest {
    private ClienteEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new ClienteEventListener();
    }

    @Test
    @DisplayName("onClienteRegistrado() no debe lanzar excepción al recibir un evento válido")
    void onClienteRegistradoNoDebeLanzarExcepcion() {
        ClienteRegistradoEvent event = ClienteRegistradoEvent.of(
                UUID.randomUUID(), "juan@banco.com", "Juan Pérez");

        assertThatNoException().isThrownBy(() -> listener.onClienteRegistrado(event));
    }

    @Test
    @DisplayName("onClienteActualizado() no debe lanzar excepción al recibir un evento válido")
    void onClienteActualizadoNoDebeLanzarExcepcion() {
        ClienteActualizadoEvent event = ClienteActualizadoEvent.of(
                UUID.randomUUID(), List.of("email", "telefono"));

        assertThatNoException().isThrownBy(() -> listener.onClienteActualizado(event));
    }

}
