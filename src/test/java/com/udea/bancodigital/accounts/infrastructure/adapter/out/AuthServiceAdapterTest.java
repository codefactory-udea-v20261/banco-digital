package com.udea.bancodigital.accounts.infrastructure.adapter.out;

import com.udea.bancodigital.shared.security.AuthenticatedUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("AuthServiceAdapter")
class AuthServiceAdapterTest {

    private final AuthServiceAdapter adapter = new AuthServiceAdapter();

    @Nested
    @DisplayName("getClienteId - success cases")
    class SuccessTest {

        @Test
        @DisplayName("Debe retornar clienteId cuando el usuario está autenticado con clienteId")
        void debeRetornarClienteIdCuandoAutenticado() {
            UUID userId = UUID.randomUUID();
            UUID clienteId = UUID.randomUUID();
            AuthenticatedUser principal = new AuthenticatedUser(userId, "testuser", clienteId);

            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn(principal);

            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
                mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                UUID result = adapter.getClienteId();
                assertThat(result).isEqualTo(clienteId);
            }
        }

        @Test
        @DisplayName("Debe retornar clienteId diferente para cada usuario")
        void debeRetornarClienteIdDiferenteParaCadaUsuario() {
            UUID userId1 = UUID.randomUUID();
            UUID clienteId1 = UUID.randomUUID();
            AuthenticatedUser principal1 = new AuthenticatedUser(userId1, "user1", clienteId1);

            Authentication authentication1 = mock(Authentication.class);
            when(authentication1.getPrincipal()).thenReturn(principal1);

            SecurityContext securityContext1 = mock(SecurityContext.class);
            when(securityContext1.getAuthentication()).thenReturn(authentication1);

            try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
                mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext1);
                UUID result1 = adapter.getClienteId();
                assertThat(result1).isEqualTo(clienteId1);
            }

            UUID userId2 = UUID.randomUUID();
            UUID clienteId2 = UUID.randomUUID();
            AuthenticatedUser principal2 = new AuthenticatedUser(userId2, "user2", clienteId2);

            Authentication authentication2 = mock(Authentication.class);
            when(authentication2.getPrincipal()).thenReturn(principal2);

            SecurityContext securityContext2 = mock(SecurityContext.class);
            when(securityContext2.getAuthentication()).thenReturn(authentication2);

            try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
                mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext2);
                UUID result2 = adapter.getClienteId();
                assertThat(result2).isEqualTo(clienteId2);
            }
        }
    }

    @Nested
    @DisplayName("getClienteId - failure cases")
    class FailureTest {

        @Test
        @DisplayName("Debe lanzar excepción cuando no hay autenticación")
        void debeLanzarExcepcionCuandoNoHayAutenticacion() {
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(null);

            try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
                mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                assertThatThrownBy(() -> adapter.getClienteId())
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("No hay un usuario autenticado en el contexto de seguridad");
            }
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el principal no es AuthenticatedUser")
        void debeLanzarExcepcionCuandoPrincipalNoEsAuthenticatedUser() {
            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn("not-a-user");

            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
                mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                assertThatThrownBy(() -> adapter.getClienteId())
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("No hay un usuario autenticado en el contexto de seguridad");
            }
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando AuthenticatedUser tiene clienteId null")
        void debeLanzarExcepcionCuandoClienteIdNull() {
            UUID userId = UUID.randomUUID();
            AuthenticatedUser principal = new AuthenticatedUser(userId, "testuser", null);

            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn(principal);

            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
                mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                assertThatThrownBy(() -> adapter.getClienteId())
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("El usuario autenticado no tiene un cliente asociado");
            }
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando Authentication es null en SecurityContext")
        void debeLanzarExcepcionCuandoAuthenticationNull() {
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(null);

            try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
                mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                assertThatThrownBy(() -> adapter.getClienteId())
                        .isInstanceOf(IllegalStateException.class);
            }
        }
    }
}
