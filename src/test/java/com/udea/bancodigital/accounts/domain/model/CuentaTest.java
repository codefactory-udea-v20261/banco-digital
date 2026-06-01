package com.udea.bancodigital.accounts.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Cuenta")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {

    @Nested
    @DisplayName("crearNueva()")
    class CrearNuevaTest {

        @Test
        @DisplayName("Debe crear cuenta con valores correctos")
        void debeCrearCuentaConValoresCorrectos() {
            UUID clienteId = UUID.randomUUID();
            Cuenta cuenta = Cuenta.crearNueva(clienteId, TipoCuenta.AHORRO, "CO4051234567890");
            assertThat(cuenta.getId()).isNotNull();
            assertThat(cuenta.getClienteId()).isEqualTo(clienteId);
            assertThat(cuenta.getTipoCuenta()).isEqualTo(TipoCuenta.AHORRO);
            assertThat(cuenta.getNumeroCuenta()).isEqualTo("CO4051234567890");
            assertThat(cuenta.getSaldo()).isEqualByComparingTo(new BigDecimal("100000"));
            assertThat(cuenta.getEstado()).isEqualTo(EstadoCuenta.ACTIVA);
            assertThat(cuenta.getFechaApertura()).isEqualTo(LocalDate.now());
        }

        @Test
        @DisplayName("Cada cuenta creada debe tener un ID único")
        void cadaCuentaDebeTenerIdUnico() {
            UUID clienteId = UUID.randomUUID();
            Cuenta cuenta1 = Cuenta.crearNueva(clienteId, TipoCuenta.AHORRO, "CO4051234567890");
            Cuenta cuenta2 = Cuenta.crearNueva(clienteId, TipoCuenta.CORRIENTE, "CO4059876543210");
            assertThat(cuenta1.getId()).isNotEqualTo(cuenta2.getId());
        }

        @Test
        @DisplayName("Debe fallar si clienteId es null")
        void debeFallarSiClienteIdEsNull() {
            // FIX Sonar S5960: extraer args que no lanzan excepción fuera de la lambda
            UUID nullId = null;
            assertThatThrownBy(() -> Cuenta.crearNueva(nullId, TipoCuenta.AHORRO, "CO4051234567890"))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("clienteId");
        }

        @Test
        @DisplayName("Debe fallar si tipoCuenta es null")
        void debeFallarSiTipoCuentaEsNull() {
            // FIX Sonar S5960: capturar UUID fuera de la lambda
            UUID clienteId = UUID.randomUUID();
            TipoCuenta nullTipo = null;
            assertThatThrownBy(() -> Cuenta.crearNueva(clienteId, nullTipo, "CO4051234567890"))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("tipoCuenta");
        }

        // FIX Sonar S5976: unificar 3 tests repetidos de numeroCuenta inválido en un
        // @ParameterizedTest
        @ParameterizedTest(name = "numeroCuenta inválido: \"{0}\"")
        @DisplayName("Debe fallar si numeroCuenta es null, vacío o solo espacios")
        @ValueSource(strings = { "", "   " })
        void debeFallarSiNumeroCuentaEsInvalido(String numeroCuenta) {
            UUID clienteId = UUID.randomUUID();
            assertThatThrownBy(() -> Cuenta.crearNueva(clienteId, TipoCuenta.AHORRO, numeroCuenta))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("numeroCuenta");
        }

        @Test
        @DisplayName("Debe fallar si numeroCuenta es null")
        void debeFallarSiNumeroCuentaEsNull() {
            UUID clienteId = UUID.randomUUID();
            assertThatThrownBy(() -> Cuenta.crearNueva(clienteId, TipoCuenta.AHORRO, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("numeroCuenta");
        }

        @Test
        @DisplayName("Debe crear cuenta tipo CORRIENTE")
        void debeCrearCuentaTipoCorriente() {
            Cuenta cuenta = Cuenta.crearNueva(UUID.randomUUID(), TipoCuenta.CORRIENTE, "CO4051234567890");
            assertThat(cuenta.getTipoCuenta()).isEqualTo(TipoCuenta.CORRIENTE);
        }
    }

    @Nested
    @DisplayName("isActiva()")
    class IsActivaTest {

        @Test
        @DisplayName("Debe retornar true cuando estado es ACTIVA")
        void debeRetornarTrueCuandoActiva() {
            Cuenta cuenta = Cuenta.builder().estado(EstadoCuenta.ACTIVA).build();
            assertThat(cuenta.isActiva()).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false cuando estado es INACTIVA")
        void debeRetornarFalseCuandoInactiva() {
            Cuenta cuenta = Cuenta.builder().estado(EstadoCuenta.INACTIVA).build();
            assertThat(cuenta.isActiva()).isFalse();
        }

        @Test
        @DisplayName("Debe retornar false cuando estado es BLOQUEADA")
        void debeRetornarFalseCuandoBloqueada() {
            Cuenta cuenta = Cuenta.builder().estado(EstadoCuenta.BLOQUEADA).build();
            assertThat(cuenta.isActiva()).isFalse();
        }
    }

    @Nested
    @DisplayName("toBuilder()")
    class ToBuilderTest {

        @Test
        @DisplayName("Debe permitir clonar y modificar sin afectar el original")
        void debePermitirClonarSinAfectarOriginal() {
            Cuenta original = Cuenta.builder()
                    .id(UUID.randomUUID())
                    .estado(EstadoCuenta.ACTIVA)
                    .saldo(new BigDecimal("1000"))
                    .build();
            Cuenta modificada = original.toBuilder()
                    .estado(EstadoCuenta.INACTIVA)
                    .build();
            assertThat(modificada.getId()).isEqualTo(original.getId());
            assertThat(modificada.getEstado()).isEqualTo(EstadoCuenta.INACTIVA);
            assertThat(original.getEstado()).isEqualTo(EstadoCuenta.ACTIVA);
        }
    }
}
