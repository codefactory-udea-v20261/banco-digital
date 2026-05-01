package com.udea.bancodigital.auth.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Usuario")
class UsuarioTest {

    @Nested
    @DisplayName("Builder")
    class BuilderTest {

        @Test
        @DisplayName("Debe crear Usuario con todos los campos usando builder")
        void debeCrearUsuarioConTodosLosCampos() {
            UUID id = UUID.randomUUID();
            UUID clienteId = UUID.randomUUID();
            Set<Rol> roles = Set.of(
                    Rol.builder().id((short) 1).nombre("ADMIN").build()
            );

            Usuario usuario = Usuario.builder()
                    .id(id)
                    .clienteId(clienteId)
                    .correo("test@banco.com")
                    .clave("hashedPassword")
                    .activo(true)
                    .bloqueado(false)
                    .intentosFallidos(0)
                    .secretoMfa("MFA_SECRET")
                    .mfaActivo(true)
                    .roles(roles)
                    .build();

            assertThat(usuario.getId()).isEqualTo(id);
            assertThat(usuario.getClienteId()).isEqualTo(clienteId);
            assertThat(usuario.getCorreo()).isEqualTo("test@banco.com");
            assertThat(usuario.getClave()).isEqualTo("hashedPassword");
            assertThat(usuario.isActivo()).isTrue();
            assertThat(usuario.isBloqueado()).isFalse();
            assertThat(usuario.getIntentosFallidos()).isEqualTo(0);
            assertThat(usuario.getSecretoMfa()).isEqualTo("MFA_SECRET");
            assertThat(usuario.isMfaActivo()).isTrue();
            assertThat(usuario.getRoles()).isEqualTo(roles);
        }

        @Test
        @DisplayName("Debe permitir valores null en campos opcionales")
        void debePermitirValoresNull() {
            Usuario usuario = Usuario.builder()
                    .id(null)
                    .clienteId(null)
                    .correo(null)
                    .clave(null)
                    .activo(false)
                    .bloqueado(false)
                    .intentosFallidos(null)
                    .secretoMfa(null)
                    .mfaActivo(false)
                    .roles(null)
                    .build();

            assertThat(usuario.getId()).isNull();
            assertThat(usuario.getClienteId()).isNull();
            assertThat(usuario.getCorreo()).isNull();
            assertThat(usuario.getClave()).isNull();
            assertThat(usuario.getIntentosFallidos()).isNull();
            assertThat(usuario.getSecretoMfa()).isNull();
            assertThat(usuario.getRoles()).isNull();
        }

        @Test
        @DisplayName("Debe crear Usuario con conjunto vacío de roles")
        void debeCrearConRolesVacios() {
            Usuario usuario = Usuario.builder()
                    .id(UUID.randomUUID())
                    .correo("test@banco.com")
                    .clave("hash")
                    .roles(Collections.emptySet())
                    .build();

            assertThat(usuario.getRoles()).isNotNull();
            assertThat(usuario.getRoles()).isEmpty();
        }

        @Test
        @DisplayName("Debe crear Usuario con múltiples roles")
        void debeCrearConMultiplesRoles() {
            Set<Rol> roles = new HashSet<>();
            roles.add(Rol.builder().id((short) 1).nombre("ADMIN").build());
            roles.add(Rol.builder().id((short) 2).nombre("USER").build());
            roles.add(Rol.builder().id((short) 4).nombre("AUDITOR").build());

            Usuario usuario = Usuario.builder()
                    .id(UUID.randomUUID())
                    .correo("multi@banco.com")
                    .clave("hash")
                    .roles(roles)
                    .build();

            assertThat(usuario.getRoles()).hasSize(3);
            assertThat(usuario.getRoles()).extracting(Rol::getNombre)
                    .containsExactlyInAnyOrder("ADMIN", "USER", "AUDITOR");
        }
    }

    @Nested
    @DisplayName("Equality (@Value)")
    class EqualityTest {

        @Test
        @DisplayName("Debe ser igual a otro Usuario con los mismos atributos")
        void debeSerIgualConMismosAtributos() {
            UUID id = UUID.randomUUID();
            Set<Rol> roles = Set.of(Rol.builder().id((short) 1).nombre("USER").build());

            Usuario usuario1 = Usuario.builder()
                    .id(id)
                    .clienteId(UUID.randomUUID())
                    .correo("test@banco.com")
                    .clave("hash")
                    .activo(true)
                    .bloqueado(false)
                    .intentosFallidos(0)
                    .secretoMfa("SECRET")
                    .mfaActivo(false)
                    .roles(roles)
                    .build();

            Usuario usuario2 = Usuario.builder()
                    .id(id)
                    .clienteId(usuario1.getClienteId())
                    .correo("test@banco.com")
                    .clave("hash")
                    .activo(true)
                    .bloqueado(false)
                    .intentosFallidos(0)
                    .secretoMfa("SECRET")
                    .mfaActivo(false)
                    .roles(roles)
                    .build();

            assertThat(usuario1).isEqualTo(usuario2);
            assertThat(usuario1.hashCode()).isEqualTo(usuario2.hashCode());
        }

        @Test
        @DisplayName("No debe ser igual si los atributos difieren - correo")
        void noDebeSerIgualSiCorreoDifiere() {
            UUID id = UUID.randomUUID();
            Usuario usuario1 = Usuario.builder()
                    .id(id)
                    .correo("user1@banco.com")
                    .clave("hash1")
                    .activo(true)
                    .build();

            Usuario usuario2 = Usuario.builder()
                    .id(id)
                    .correo("user2@banco.com")
                    .clave("hash1")
                    .activo(true)
                    .build();

            assertThat(usuario1).isNotEqualTo(usuario2);
        }

        @Test
        @DisplayName("No debe ser igual si los atributos difieren - clave")
        void noDebeSerIgualSiClaveDifiere() {
            UUID id = UUID.randomUUID();
            Usuario usuario1 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash1")
                    .activo(true)
                    .build();

            Usuario usuario2 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash2")
                    .activo(true)
                    .build();

            assertThat(usuario1).isNotEqualTo(usuario2);
        }

        @Test
        @DisplayName("No debe ser igual si los atributos difieren - activo")
        void noDebeSerIgualSiActivoDifiere() {
            UUID id = UUID.randomUUID();
            Usuario usuario1 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash")
                    .activo(true)
                    .build();

            Usuario usuario2 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash")
                    .activo(false)
                    .build();

            assertThat(usuario1).isNotEqualTo(usuario2);
        }

        @Test
        @DisplayName("No debe ser igual si los atributos difieren - bloqueado")
        void noDebeSerIgualSiBloqueadoDifiere() {
            UUID id = UUID.randomUUID();
            Usuario usuario1 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash")
                    .bloqueado(false)
                    .build();

            Usuario usuario2 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash")
                    .bloqueado(true)
                    .build();

            assertThat(usuario1).isNotEqualTo(usuario2);
        }

        @Test
        @DisplayName("No debe ser igual si los atributos difieren - intentosFallidos")
        void noDebeSerIgualSiIntentosFallidosDifieren() {
            UUID id = UUID.randomUUID();
            Usuario usuario1 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash")
                    .intentosFallidos(0)
                    .build();

            Usuario usuario2 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash")
                    .intentosFallidos(3)
                    .build();

            assertThat(usuario1).isNotEqualTo(usuario2);
        }

        @Test
        @DisplayName("No debe ser igual si los atributos difieren - secretoMfa")
        void noDebeSerIgualSiSecretoMfaDifiere() {
            UUID id = UUID.randomUUID();
            Usuario usuario1 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash")
                    .secretoMfa("SECRET1")
                    .build();

            Usuario usuario2 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash")
                    .secretoMfa("SECRET2")
                    .build();

            assertThat(usuario1).isNotEqualTo(usuario2);
        }

        @Test
        @DisplayName("No debe ser igual si los atributos difieren - mfaActivo")
        void noDebeSerIgualSiMfaActivoDifiere() {
            UUID id = UUID.randomUUID();
            Usuario usuario1 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash")
                    .mfaActivo(true)
                    .build();

            Usuario usuario2 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash")
                    .mfaActivo(false)
                    .build();

            assertThat(usuario1).isNotEqualTo(usuario2);
        }

        @Test
        @DisplayName("No debe ser igual si los atributos difieren - roles")
        void noDebeSerIgualSiRolesDifieren() {
            UUID id = UUID.randomUUID();
            Usuario usuario1 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash")
                    .roles(Set.of(Rol.builder().id((short) 1).nombre("ADMIN").build()))
                    .build();

            Usuario usuario2 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash")
                    .roles(Set.of(Rol.builder().id((short) 2).nombre("USER").build()))
                    .build();

            assertThat(usuario1).isNotEqualTo(usuario2);
        }

        @Test
        @DisplayName("No debe ser igual si los atributos difieren - roles null vs not null")
        void noDebeSerIgualSiRolesNullVsNotNull() {
            UUID id = UUID.randomUUID();
            Usuario usuario1 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash")
                    .roles(null)
                    .build();

            Usuario usuario2 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash")
                    .roles(Set.of(Rol.builder().id((short) 1).nombre("ADMIN").build()))
                    .build();

            assertThat(usuario1).isNotEqualTo(usuario2);
        }

        @Test
        @DisplayName("No debe ser igual a null")
        void noDebeSerIgualANull() {
            Usuario usuario = Usuario.builder()
                    .id(UUID.randomUUID())
                    .correo("test@banco.com")
                    .build();

            assertThat(usuario).isNotEqualTo(null);
        }

        @Test
        @DisplayName("No debe ser igual a un objeto de otra clase")
        void noDebeSerIgualAOtroTipo() {
            Usuario usuario = Usuario.builder()
                    .id(UUID.randomUUID())
                    .correo("test@banco.com")
                    .build();

            assertThat(usuario).isNotEqualTo("string");
        }

        @Test
        @DisplayName("Debe tener hashCode consistente")
        void debeTenerHashCodeConsistente() {
            UUID id = UUID.randomUUID();
            Usuario usuario = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash")
                    .build();

            int hashCode1 = usuario.hashCode();
            int hashCode2 = usuario.hashCode();

            assertThat(hashCode1).isEqualTo(hashCode2);
        }

        @Test
        @DisplayName("hashCode debe ser igual para objetos iguales")
        void hashCodeDebeSerIgualParaObjetosIguales() {
            UUID id = UUID.randomUUID();
            Set<Rol> roles = Set.of(Rol.builder().id((short) 1).nombre("USER").build());

            Usuario usuario1 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash")
                    .roles(roles)
                    .build();

            Usuario usuario2 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash")
                    .roles(roles)
                    .build();

            assertThat(usuario1.hashCode()).isEqualTo(usuario2.hashCode());
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("Debe incluir los campos en toString")
        void debeIncluirCamposEnToString() {
            Usuario usuario = Usuario.builder()
                    .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                    .correo("test@banco.com")
                    .activo(true)
                    .build();

            String result = usuario.toString();

            assertThat(result).contains("550e8400-e29b-41d4-a716-446655440000");
            assertThat(result).contains("test@banco.com");
        }

        @Test
        @DisplayName("toString debe incluir la clave (comportamiento actual de @Value)")
        void toStringDebeIncluirClave() {
            Usuario usuario = Usuario.builder()
                    .id(UUID.randomUUID())
                    .correo("test@banco.com")
                    .clave("hashedPassword123")
                    .build();

            String result = usuario.toString();

            assertThat(result).contains("hashedPassword123");
        }

        @Test
        @DisplayName("toString debe funcionar con valores null")
        void toStringConValoresNull() {
            Usuario usuario = Usuario.builder()
                    .id(null)
                    .correo(null)
                    .clave(null)
                    .build();

            String result = usuario.toString();

            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("Additional Edge Cases")
    class EdgeCasesTest {

        @Test
        @DisplayName("Debe ser igual comparando con mismo objeto")
        void debeSerIgualMismoObjeto() {
            Usuario usuario = Usuario.builder()
                    .id(UUID.randomUUID())
                    .correo("test@banco.com")
                    .clave("hash")
                    .build();

            assertThat(usuario).isEqualTo(usuario);
        }

        @Test
        @DisplayName("hashCode deve manejar campos null")
        void hashCodeDebeManejarCamposNull() {
            Usuario usuario = Usuario.builder()
                    .id(null)
                    .correo(null)
                    .clave(null)
                    .roles(null)
                    .build();

            // Should not throw exception
            int hashCode = usuario.hashCode();
            assertThat(hashCode).isNotNull();
        }

        @Test
        @DisplayName("toString con todos los campos null")
        void toStringTodosLosCamposNull() {
            Usuario usuario = Usuario.builder()
                    .id(null)
                    .clienteId(null)
                    .correo(null)
                    .clave(null)
                    .activo(false)
                    .bloqueado(false)
                    .intentosFallidos(null)
                    .secretoMfa(null)
                    .mfaActivo(false)
                    .roles(null)
                    .build();

            String result = usuario.toString();
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("equals debe retornar false cuando this.correo es null y other.correo no es null")
        void equalsConCorreoNullVsNotNull() {
            UUID id = UUID.randomUUID();
            Usuario usuario1 = Usuario.builder()
                    .id(id)
                    .correo(null)
                    .clave("hash")
                    .build();

            Usuario usuario2 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash")
                    .build();

            assertThat(usuario1).isNotEqualTo(usuario2);
        }

        @Test
        @DisplayName("equals debe retornar false cuando this.clave es null y other.clave no es null")
        void equalsConClaveNullVsNotNull() {
            UUID id = UUID.randomUUID();
            Usuario usuario1 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave(null)
                    .build();

            Usuario usuario2 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave("hash")
                    .build();

            assertThat(usuario1).isNotEqualTo(usuario2);
        }

        @Test
        @DisplayName("equals debe retornar true cuando ambos correo son null")
        void equalsConAmbosCorreoNull() {
            UUID id = UUID.randomUUID();
            Usuario usuario1 = Usuario.builder()
                    .id(id)
                    .correo(null)
                    .clave("hash")
                    .build();

            Usuario usuario2 = Usuario.builder()
                    .id(id)
                    .correo(null)
                    .clave("hash")
                    .build();

            assertThat(usuario1).isEqualTo(usuario2);
        }

        @Test
        @DisplayName("equals debe retornar true cuando ambos clave son null")
        void equalsConAmbosClaveNull() {
            UUID id = UUID.randomUUID();
            Usuario usuario1 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave(null)
                    .build();

            Usuario usuario2 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .clave(null)
                    .build();

            assertThat(usuario1).isEqualTo(usuario2);
        }

        @Test
        @DisplayName("equals debe retornar false cuando this.roles es null y other.roles no es null")
        void equalsConRolesNullVsNotNull() {
            UUID id = UUID.randomUUID();
            Usuario usuario1 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .roles(null)
                    .build();

            Usuario usuario2 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .roles(Set.of(Rol.builder().id((short) 1).nombre("USER").build()))
                    .build();

            assertThat(usuario1).isNotEqualTo(usuario2);
        }

        @Test
        @DisplayName("equals debe retornar true cuando ambos roles son null")
        void equalsConAmbosRolesNull() {
            UUID id = UUID.randomUUID();
            Usuario usuario1 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .roles(null)
                    .build();

            Usuario usuario2 = Usuario.builder()
                    .id(id)
                    .correo("test@banco.com")
                    .roles(null)
                    .build();

            assertThat(usuario1).isEqualTo(usuario2);
        }

        @Test
        @DisplayName("equals debe retornar false cuando this.clienteId es null y other.clienteId no es null")
        void equalsConClienteIdNullVsNotNull() {
            UUID id = UUID.randomUUID();
            Usuario usuario1 = Usuario.builder()
                    .id(id)
                    .clienteId(null)
                    .correo("test@banco.com")
                    .build();

            Usuario usuario2 = Usuario.builder()
                    .id(id)
                    .clienteId(UUID.randomUUID())
                    .correo("test@banco.com")
                    .build();

            assertThat(usuario1).isNotEqualTo(usuario2);
        }

        @Test
        @DisplayName("equals debe retornar true cuando ambos clienteId son null")
        void equalsConAmbosClienteIdNull() {
            UUID id = UUID.randomUUID();
            Usuario usuario1 = Usuario.builder()
                    .id(id)
                    .clienteId(null)
                    .correo("test@banco.com")
                    .build();

            Usuario usuario2 = Usuario.builder()
                    .id(id)
                    .clienteId(null)
                    .correo("test@banco.com")
                    .build();

            assertThat(usuario1).isEqualTo(usuario2);
        }
    }
}
