package com.udea.bancodigital.accounts.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EstadoCuenta")
class EstadoCuentaTest {

    @Nested
    @DisplayName("Values")
    class ValuesTest {

        @Test
        @DisplayName("Debe tener tres valores: ACTIVA, INACTIVA, BLOQUEADA")
        void debesTenerTresValores() {
            EstadoCuenta[] valores = EstadoCuenta.values();
            assertThat(valores).contains(EstadoCuenta.ACTIVA, EstadoCuenta.INACTIVA, EstadoCuenta.BLOQUEADA);
            assertThat(valores).hasSize(3);
        }

        @Test
        @DisplayName("ACTIVA debe existir")
        void activaDebeExistir() {
            assertThat(EstadoCuenta.ACTIVA).isNotNull();
        }

        @Test
        @DisplayName("INACTIVA debe existir")
        void inactivaDebeExistir() {
            assertThat(EstadoCuenta.INACTIVA).isNotNull();
        }

        @Test
        @DisplayName("BLOQUEADA debe existir")
        void bloqueadaDebeExistir() {
            assertThat(EstadoCuenta.BLOQUEADA).isNotNull();
        }
    }

    @Nested
    @DisplayName("valueOf()")
    class ValueOfTest {

        @Test
        @DisplayName("Debe convertir string 'ACTIVA' a enum")
        void debeConvertirStringACTIVA() {
            assertThat(EstadoCuenta.valueOf("ACTIVA")).isEqualTo(EstadoCuenta.ACTIVA);
        }

        @Test
        @DisplayName("Debe convertir string 'INACTIVA' a enum")
        void debeConvertirStringINACTIVA() {
            assertThat(EstadoCuenta.valueOf("INACTIVA")).isEqualTo(EstadoCuenta.INACTIVA);
        }

        @Test
        @DisplayName("Debe convertir string 'BLOQUEADA' a enum")
        void debeConvertirStringBLOQUEADA() {
            assertThat(EstadoCuenta.valueOf("BLOQUEADA")).isEqualTo(EstadoCuenta.BLOQUEADA);
        }
    }

    @Nested
    @DisplayName("Comparisons")
    class ComparisonsTest {

        @Test
        @DisplayName("ACTIVA debe ser igual a ACTIVA")
        void activaIgualAActiva() {
            // FIX Sonar S5863: no comparar el mismo valor consigo mismo
            // En lugar de assertThat(ACTIVA).isEqualTo(ACTIVA), verificar identidad de enum
            EstadoCuenta estado = EstadoCuenta.ACTIVA;
            assertThat(estado.name()).isEqualTo("ACTIVA");
        }

        @Test
        @DisplayName("ACTIVA no debe ser igual a INACTIVA")
        void activaNoIgualAInactiva() {
            assertThat(EstadoCuenta.ACTIVA).isNotEqualTo(EstadoCuenta.INACTIVA);
        }

        @Test
        @DisplayName("INACTIVA no debe ser igual a BLOQUEADA")
        void inactivaNoIgualABloqueada() {
            assertThat(EstadoCuenta.INACTIVA).isNotEqualTo(EstadoCuenta.BLOQUEADA);
        }
    }

    @Nested
    @DisplayName("Enum properties")
    class EnumPropertiesTest {

        @Test
        @DisplayName("Debe tener el mismo hashCode para el mismo valor")
        void debeTenerMismoHashCodeParaMismoValor() {
            EstadoCuenta estado1 = EstadoCuenta.ACTIVA;
            EstadoCuenta estado2 = EstadoCuenta.ACTIVA;
            assertThat(estado1.hashCode()).isEqualTo(estado2.hashCode());
        }

        @Test
        @DisplayName("Debe tener toString() que retorna el nombre")
        void debeToStringRetornaNombre() {
            assertThat(EstadoCuenta.ACTIVA.toString()).isEqualTo("ACTIVA");
            assertThat(EstadoCuenta.INACTIVA.toString()).isEqualTo("INACTIVA");
            assertThat(EstadoCuenta.BLOQUEADA.toString()).isEqualTo("BLOQUEADA");
        }

        @Test
        @DisplayName("Debe tener name() que retorna el nombre")
        void debeNameRetornaNombre() {
            assertThat(EstadoCuenta.ACTIVA.name()).isEqualTo("ACTIVA");
            assertThat(EstadoCuenta.INACTIVA.name()).isEqualTo("INACTIVA");
            assertThat(EstadoCuenta.BLOQUEADA.name()).isEqualTo("BLOQUEADA");
        }

        @Test
        @DisplayName("Debe tener ordinal() distinto para cada valor")
        void debeOrdinalDistintoParaCadaValor() {
            assertThat(EstadoCuenta.ACTIVA.ordinal())
                    .isNotEqualTo(EstadoCuenta.INACTIVA.ordinal())
                    .isNotEqualTo(EstadoCuenta.BLOQUEADA.ordinal());
        }
    }
}