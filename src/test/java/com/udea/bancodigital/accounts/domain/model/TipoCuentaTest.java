package com.udea.bancodigital.accounts.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TipoCuenta")
class TipoCuentaTest {
    @Nested
    @DisplayName("fromId()")
    class FromIdTest {

        @Test
        @DisplayName("Debe retornar AHORRO para id 1")
        void debeRetornarAhorro() {
            assertThat(TipoCuenta.fromId((short) 1)).isEqualTo(TipoCuenta.AHORRO);
        }

        @Test
        @DisplayName("Debe retornar CORRIENTE para id 2")
        void debeRetornarCorriente() {
            assertThat(TipoCuenta.fromId((short) 2)).isEqualTo(TipoCuenta.CORRIENTE);
        }

        @Test
        @DisplayName("Debe retornar null cuando id es null")
        void debeRetornarNullCuandoIdNull() {
            assertThat(TipoCuenta.fromId(null)).isNull();
        }

        @Test
        @DisplayName("Debe lanzar excepción para id inexistente")
        void debeLanzarExcepcionParaIdInexistente() {
            assertThatThrownBy(() -> TipoCuenta.fromId((short) 99))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("fromNombre()")
    class FromNombreTest {

        @Test
        @DisplayName("Debe retornar AHORRO para nombre 'AHORRO'")
        void debeRetornarAhorroPorNombre() {
            assertThat(TipoCuenta.fromNombre("AHORRO")).isEqualTo(TipoCuenta.AHORRO);
        }

        @Test
        @DisplayName("Debe retornar CORRIENTE para nombre 'CORRIENTE'")
        void debeRetornarCorrientePorNombre() {
            assertThat(TipoCuenta.fromNombre("CORRIENTE")).isEqualTo(TipoCuenta.CORRIENTE);
        }

        @Test
        @DisplayName("Debe normalizar 'AHORROS' a AHORRO")
        void debeNormalizarAhorros() {
            assertThat(TipoCuenta.fromNombre("AHORROS")).isEqualTo(TipoCuenta.AHORRO);
        }

        @Test
        @DisplayName("Debe normalizar minúsculas a mayúsculas")
        void debeNormalizarMinusculas() {
            assertThat(TipoCuenta.fromNombre("ahorro")).isEqualTo(TipoCuenta.AHORRO);
        }

        @Test
        @DisplayName("Debe ignorar espacios al inicio y al final")
        void debeIgnorarEspacios() {
            assertThat(TipoCuenta.fromNombre("  AHORRO  ")).isEqualTo(TipoCuenta.AHORRO);
        }

        @Test
        @DisplayName("Debe retornar null cuando nombre es null")
        void debeRetornarNullCuandoNombreNull() {
            assertThat(TipoCuenta.fromNombre(null)).isNull();
        }

        @Test
        @DisplayName("Debe retornar null cuando nombre está vacío")
        void debeRetornarNullCuandoNombreVacio() {
            assertThat(TipoCuenta.fromNombre("")).isNull();
        }

        @Test
        @DisplayName("Debe retornar null cuando nombre tiene solo espacios en blanco")
        void debeRetornarNullCuandoNombreBlanco() {
            assertThat(TipoCuenta.fromNombre("   ")).isNull();
        }

        @Test
        @DisplayName("Debe lanzar excepción para nombre inválido")
        void debeLanzarExcepcionParaNombreInvalido() {
            assertThatThrownBy(() -> TipoCuenta.fromNombre("INVERSIONES"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("INVERSIONES");
        }
    }

    @Nested
    @DisplayName("Getters")
    class GettersTest {

        @Test
        @DisplayName("AHORRO debe tener id=1 y nombre='AHORRO'")
        void ahorroDebeTenarIdYNombre() {
            assertThat(TipoCuenta.AHORRO.getId()).isEqualTo((short) 1);
            assertThat(TipoCuenta.AHORRO.getNombre()).isEqualTo("AHORRO");
        }

        @Test
        @DisplayName("CORRIENTE debe tener id=2 y nombre='CORRIENTE'")
        void corrienteDebeTenerIdYNombre() {
            assertThat(TipoCuenta.CORRIENTE.getId()).isEqualTo((short) 2);
            assertThat(TipoCuenta.CORRIENTE.getNombre()).isEqualTo("CORRIENTE");
        }
    }

}
