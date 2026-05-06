package com.udea.bancodigital.shared.health;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("EurekaHealthIndicator")
class EurekaHealthIndicatorTest {
    @Mock
    private DiscoveryClient discoveryClient;

    private EurekaHealthIndicator indicator;

    @BeforeEach
    void setUp() {
        indicator = new EurekaHealthIndicator(discoveryClient);
    }

    @Nested
    @DisplayName("Con servicios registrados")
    class ConServiciosRegistrados {

        @Test
        @DisplayName("Debe retornar UP con cantidad de servicios")
        void debeRetornarUpConCantidad() {
            when(discoveryClient.getServices()).thenReturn(List.of("core-banking", "identity"));

            Health health = indicator.health();

            assertThat(health.getStatus()).isEqualTo(Status.UP);
            assertThat(health.getDetails().get("registered_services")).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Sin servicios registrados")
    class SinServiciosRegistrados {

        @Test
        @DisplayName("Debe retornar DOWN cuando la lista está vacía")
        void debeRetornarDownCuandoListaVacia() {
            when(discoveryClient.getServices()).thenReturn(Collections.emptyList());

            Health health = indicator.health();

            assertThat(health.getStatus()).isEqualTo(Status.DOWN);
            assertThat(health.getDetails().get("eureka")).isEqualTo("No services registered");
        }

        @Test
        @DisplayName("Debe retornar DOWN cuando la lista es null")
        void debeRetornarDownCuandoListaNull() {
            when(discoveryClient.getServices()).thenReturn(null);

            Health health = indicator.health();

            assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        }
    }

    @Nested
    @DisplayName("Cuando ocurre una excepción")
    class CuandoOcurreExcepcion {

        @Test
        @DisplayName("Debe retornar DOWN con el mensaje de error")
        void debeRetornarDownConMensajeDeError() {
            when(discoveryClient.getServices()).thenThrow(new RuntimeException("Connection refused"));

            Health health = indicator.health();

            assertThat(health.getStatus()).isEqualTo(Status.DOWN);
            assertThat(health.getDetails().get("reason")).isEqualTo("Connection refused");
        }
    }

}
