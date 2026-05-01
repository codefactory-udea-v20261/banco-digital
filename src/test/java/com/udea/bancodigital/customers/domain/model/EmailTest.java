package com.udea.bancodigital.customers.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Email")
class EmailTest {

    @Nested
    @DisplayName("Constructor validation")
    class ConstructorValidationTest {

        @Test
        @DisplayName("Debe crear Email válido")
        void debeCrearEmailValido() {
            Email email = new Email("test@banco.com");
            assertThat(email.valor()).isEqualTo("test@banco.com");
        }

        @Test
        @DisplayName("Debe fallar si email es null")
        void debeFallarSiNull() {
            assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("El email no puede ser nulo");
        }

        @Test
        @DisplayName("Debe fallar si email está vacío")
        void debeFallarSiVacio() {
            assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El email no puede estar vacío");
        }

        @Test
        @DisplayName("Debe fallar si tiene solo espacios")
        void debeFallarSiSoloEspacios() {
            assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El email no puede estar vacío");
        }

        @Test
        @DisplayName("Debe fallar si email excede 255 caracteres")
        void debeFallarSiExcede255Caracteres() {
            String longEmail = "a".repeat(250) + "@b.com";
            assertThatThrownBy(() -> new Email(longEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("255");
        }

        @Test
        @DisplayName("Debe fallar si email no tiene formato válido")
        void debeFallarSiFormatoInvalido() {
            assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("formato válido");
        }

        @Test
        @DisplayName("Debe fallar si email no tiene @")
        void debeFallarSiNoTieneArroba() {
            assertThatThrownBy(() -> new Email("invalidemail.com"))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Debe fallar si email no tiene dominio")
        void debeFallarSiNoTieneDominio() {
            assertThatThrownBy(() -> new Email("invalid@"))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Debe aceptar email con números")
        void debeAceptarEmailConNumeros() {
            Email email = new Email("test123@banco123.com");
            assertThat(email.valor()).isEqualTo("test123@banco123.com");
        }

        @Test
        @DisplayName("Debe aceptar email con puntos y guiones")
        void debeAceptarEmailConPuntosYGuiones() {
            Email email = new Email("test.user-name@banco.com");
            assertThat(email.valor()).isEqualTo("test.user-name@banco.com");
        }
    }

    @Nested
    @DisplayName("Factory method 'of'")
    class OfMethodTest {

        @Test
        @DisplayName("Debe crear Email usando factory method")
        void debeCrearUsandoOf() {
            Email email = Email.of("test@banco.com");
            assertThat(email.valor()).isEqualTo("test@banco.com");
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("Debe retornar el valor en toString")
        void debeRetornarValorEnToString() {
            Email email = new Email("test@banco.com");
            assertThat(email.toString()).isEqualTo("test@banco.com");
        }
    }

    @Nested
    @DisplayName("Equals and hashCode (record)")
    class EqualsAndHashCodeTest {

        @Test
        @DisplayName("Debe ser igual si mismo objeto")
        void debeSerIgualMismoObjeto() {
            Email email = new Email("test@banco.com");
            assertThat(email).isEqualTo(email);
        }

        @Test
        @DisplayName("Debe ser igual si mismo valor")
        void debeSerIgualMismoValor() {
            Email email1 = new Email("test@banco.com");
            Email email2 = new Email("test@banco.com");
            assertThat(email1).isEqualTo(email2).hasSameHashCodeAs(email2);
        }

        @Test
        @DisplayName("No debe ser igual si valor difiere")
        void noDebeSerIgualSiValorDifiere() {
            Email email1 = new Email("test1@banco.com");
            Email email2 = new Email("test2@banco.com");
            assertThat(email1).isNotEqualTo(email2);
        }

        @Test
        @DisplayName("No debe ser igual a null")
        void noDebeSerIgualANull() {
            Email email = new Email("test@banco.com");
            assertThat(email).isNotEqualTo(null);
        }
    }
}
