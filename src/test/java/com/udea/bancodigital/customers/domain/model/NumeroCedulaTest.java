package com.udea.bancodigital.customers.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("NumeroCedula")
class NumeroCedulaTest {

    @Nested
    @DisplayName("Constructor validation")
    class ConstructorValidationTest {

        @Test
        @DisplayName("Debe crear NumeroCedula válido")
        void debeCrearNumeroCedulaValido() {
            NumeroCedula cedula = new NumeroCedula("12345678");
            assertThat(cedula.valor()).isEqualTo("12345678");
        }

        @Test
        @DisplayName("Debe crear NumeroCedula con 7 dígitos (mínimo)")
        void debeCrearCon7Digitos() {
            NumeroCedula cedula = new NumeroCedula("1234567");
            assertThat(cedula.valor()).isEqualTo("1234567");
        }

        @Test
        @DisplayName("Debe crear NumeroCedula con 20 dígitos (máximo)")
        void debeCrearCon20Digitos() {
            NumeroCedula cedula = new NumeroCedula("12345678901234567890");
            assertThat(cedula.valor()).isEqualTo("12345678901234567890");
        }

        @Test
        @DisplayName("Debe fallar si es null")
        void debeFallarSiNull() {
            assertThatThrownBy(() -> new NumeroCedula(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("El número de cédula no puede ser nulo");
        }

        @Test
        @DisplayName("Debe fallar si está vacío")
        void debeFallarSiVacio() {
            assertThatThrownBy(() -> new NumeroCedula(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El número de cédula no puede estar vacío");
        }

        @Test
        @DisplayName("Debe fallar si tiene solo espacios")
        void debeFallarSiSoloEspacios() {
            assertThatThrownBy(() -> new NumeroCedula("   "))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Debe fallar si tiene menos de 7 dígitos")
        void debeFallarSiMenosDe7Digitos() {
            assertThatThrownBy(() -> new NumeroCedula("123456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("7 y 20");
        }

        @Test
        @DisplayName("Debe fallar si tiene más de 20 dígitos")
        void debeFallarSiMasDe20Digitos() {
            assertThatThrownBy(() -> new NumeroCedula("123456789012345678901"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("7 y 20");
        }

        @Test
        @DisplayName("Debe fallar si contiene letras")
        void debeFallarSiContieneLetras() {
            assertThatThrownBy(() -> new NumeroCedula("1234567a"))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Debe fallar si contiene caracteres especiales")
        void debeFallarSiContieneCaracteresEspeciales() {
            assertThatThrownBy(() -> new NumeroCedula("1234-567"))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Factory method 'of'")
    class OfMethodTest {

        @Test
        @DisplayName("Debe crear usando factory method")
        void debeCrearUsandoOf() {
            NumeroCedula cedula = NumeroCedula.of("12345678");
            assertThat(cedula.valor()).isEqualTo("12345678");
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("Debe retornar el valor en toString")
        void debeRetornarValorEnToString() {
            NumeroCedula cedula = new NumeroCedula("12345678");
            assertThat(cedula.toString()).isEqualTo("12345678");
        }
    }

    @Nested
    @DisplayName("Equals and hashCode (record)")
    class EqualsAndHashCodeTest {

        @Test
        @DisplayName("Debe ser igual si mismo objeto")
        void debeSerIgualMismoObjeto() {
            NumeroCedula cedula1 = new NumeroCedula("12345678");
            NumeroCedula cedula2 = new NumeroCedula("12345678");
            assertThat(cedula1).isEqualTo(cedula2);
        }

        @Test
        @DisplayName("Debe ser igual si mismo valor")
        void debeSerIgualMismoValor() {
            NumeroCedula c1 = new NumeroCedula("12345678");
            NumeroCedula c2 = new NumeroCedula("12345678");
            assertThat(c1).isEqualTo(c2).hasSameHashCodeAs(c2);
        }

        @Test
        @DisplayName("No debe ser igual si valor difiere")
        void noDebeSerIgualSiValorDifiere() {
            NumeroCedula c1 = new NumeroCedula("12345678");
            NumeroCedula c2 = new NumeroCedula("87654321");
            assertThat(c1).isNotEqualTo(c2);
        }

        @Test
        @DisplayName("No debe ser igual a null")
        void noDebeSerIgualANull() {
            NumeroCedula cedula = new NumeroCedula("12345678");
            assertThat(cedula).isNotEqualTo(null);
        }
    }
}
