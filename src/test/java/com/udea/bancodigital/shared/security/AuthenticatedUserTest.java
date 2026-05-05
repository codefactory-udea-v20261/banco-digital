package com.udea.bancodigital.shared.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuthenticatedUser")
class AuthenticatedUserTest {
    @Test
    @DisplayName("Debe exponer todos los campos correctamente")
    void debeExponerTodosLosCampos() {
        UUID userId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();

        AuthenticatedUser user = new AuthenticatedUser(userId, "juanperez", clienteId);

        assertThat(user.userId()).isEqualTo(userId);
        assertThat(user.username()).isEqualTo("juanperez");
        assertThat(user.clienteId()).isEqualTo(clienteId);
    }

    @Test
    @DisplayName("Dos instancias con mismos valores deben ser iguales (record)")
    void dosInstanciasConMismosValoresDebenSerIguales() {
        UUID userId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();

        AuthenticatedUser user1 = new AuthenticatedUser(userId, "juanperez", clienteId);
        AuthenticatedUser user2 = new AuthenticatedUser(userId, "juanperez", clienteId);

        assertThat(user1).isEqualTo(user2).hasSameHashCodeAs(user2);
    }

    @Test
    @DisplayName("Dos instancias con valores distintos no deben ser iguales")
    void dosInstanciasConValoresDistintosNoDebenSerIguales() {
        AuthenticatedUser user1 = new AuthenticatedUser(UUID.randomUUID(), "a", UUID.randomUUID());
        AuthenticatedUser user2 = new AuthenticatedUser(UUID.randomUUID(), "b", UUID.randomUUID());

        assertThat(user1).isNotEqualTo(user2);
    }

}
