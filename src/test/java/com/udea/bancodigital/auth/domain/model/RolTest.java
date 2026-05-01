package com.udea.bancodigital.auth.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Rol")
class RolTest {

    @Nested
    @DisplayName("Builder")
    class BuilderTest {

        @Test
        @DisplayName("Debe crear Rol con todos los campos usando builder")
        void debeCrearRolConTodosLosCampos() {
            Rol rol = Rol.builder()
                    .id((short) 1)
                    .nombre("ADMIN")
                    .build();

            assertThat(rol.getId()).isEqualTo((short) 1);
            assertThat(rol.getNombre()).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("Debe permitir valores null")
        void debePermitirValoresNull() {
            Rol rol = Rol.builder()
                    .id(null)
                    .nombre(null)
                    .build();

            assertThat(rol.getId()).isNull();
            assertThat(rol.getNombre()).isNull();
        }

        @Test
        @DisplayName("Debe crear todos los valores de enum mediante builder")
        void debeCrearTodosLosValoresEnum() {
            Rol rolAdmin = Rol.builder().id((short) 1).nombre("ADMIN").build();
            Rol rolUser = Rol.builder().id((short) 2).nombre("USER").build();
            Rol rolAuditor = Rol.builder().id((short) 4).nombre("AUDITOR").build();

            assertThat(rolAdmin.getNombre()).isEqualTo("ADMIN");
            assertThat(rolUser.getNombre()).isEqualTo("USER");
            assertThat(rolAuditor.getNombre()).isEqualTo("AUDITOR");
        }
    }

    @Nested
    @DisplayName("Equality (@Value)")
    class EqualityTest {

        @Test
        @DisplayName("Debe ser igual a otro Rol con los mismos atributos")
        void debeSerIgualConMismosAtributos() {
            Rol rol1 = Rol.builder().id((short) 1).nombre("ADMIN").build();
            Rol rol2 = Rol.builder().id((short) 1).nombre("ADMIN").build();

            assertThat(rol1).isEqualTo(rol2);
            assertThat(rol1.hashCode()).isEqualTo(rol2.hashCode());
        }

        @Test
        @DisplayName("No debe ser igual si el id difiere")
        void noDebeSerIgualSiIdDifiere() {
            Rol rol1 = Rol.builder().id((short) 1).nombre("ADMIN").build();
            Rol rol2 = Rol.builder().id((short) 2).nombre("ADMIN").build();

            assertThat(rol1).isNotEqualTo(rol2);
        }

        @Test
        @DisplayName("No debe ser igual si el nombre difiere")
        void noDebeSerIgualSiNombreDifiere() {
            Rol rol1 = Rol.builder().id((short) 1).nombre("ADMIN").build();
            Rol rol2 = Rol.builder().id((short) 1).nombre("USER").build();

            assertThat(rol1).isNotEqualTo(rol2);
        }

        @Test
        @DisplayName("No debe ser igual a null")
        void noDebeSerIgualANull() {
            Rol rol = Rol.builder().id((short) 1).nombre("ADMIN").build();

            assertThat(rol).isNotEqualTo(null);
        }

        @Test
        @DisplayName("No debe ser igual a un objeto de otra clase")
        void noDebeSerIgualAOtroTipo() {
            Rol rol = Rol.builder().id((short) 1).nombre("ADMIN").build();

            assertThat(rol).isNotEqualTo("string");
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("Debe incluir los campos en toString")
        void debeIncluirCamposEnToString() {
            Rol rol = Rol.builder()
                    .id((short) 1)
                    .nombre("ADMIN")
                    .build();

            String result = rol.toString();

            assertThat(result).contains("1");
            assertThat(result).contains("ADMIN");
        }
    }
}
