package com.udea.bancodigital.customers.application.usecase;

import com.udea.bancodigital.customers.application.dto.ActualizarClienteRequestDto;
import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.application.mapper.ClienteMapper;
import com.udea.bancodigital.customers.domain.event.ClienteActualizadoEvent;
import com.udea.bancodigital.customers.domain.exception.ClienteNoEncontradoException;
import com.udea.bancodigital.customers.domain.exception.ClienteYaExisteException;
import com.udea.bancodigital.customers.domain.model.Cliente;
import com.udea.bancodigital.customers.domain.model.Email;
import com.udea.bancodigital.customers.domain.port.out.ClienteRepositoryPort;
import com.udea.bancodigital.customers.domain.port.out.DomainEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActualizarClienteUseCase")
class ActualizarClienteUseCaseTest {

    @Mock
    private ClienteRepositoryPort repository;

    @Mock
    private ClienteMapper mapper;

    @Mock
    private DomainEventPublisher eventPublisher;

    private ActualizarClienteUseCase useCase;

    @Captor
    private ArgumentCaptor<Cliente> clienteCaptor;

    @Captor
    private ArgumentCaptor<ClienteActualizadoEvent> eventCaptor;

    @BeforeEach
    void setUp() {
        useCase = new ActualizarClienteUseCase(repository, mapper, eventPublisher);
    }

    @Nested
    @DisplayName("Actualizar cliente")
    class ActualizarClienteTest {

        @Test
        @DisplayName("Debe actualizar primer nombre")
        void debeActualizarPrimerNombre() {
            UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            
            Cliente clienteExistente = Cliente.builder()
                    .id(id)
                    .primerNombre("Juan")
                    .primerApellido("Perez")
                    .email(new Email("juan@email.com"))
                    .activo(true)
                    .build();

            Cliente clienteActualizado = Cliente.builder()
                    .id(id)
                    .primerNombre("Carlos")
                    .primerApellido("Perez")
                    .email(new Email("juan@email.com"))
                    .activo(true)
                    .build();

            ActualizarClienteRequestDto request = ActualizarClienteRequestDto.builder()
                    .primerNombre("Carlos")
                    .build();

            ClienteResponseDto responseDto = ClienteResponseDto.builder()
                    .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                    .primerNombre("Carlos")
                    .primerApellido("Perez")
                    .email("juan@perez.com")
                    .build();

            when(repository.findById(id)).thenReturn(java.util.Optional.of(clienteExistente));
            when(repository.save(any(Cliente.class))).thenReturn(clienteActualizado);
            when(mapper.toResponseDto(clienteActualizado)).thenReturn(responseDto);

            ClienteResponseDto result = useCase.actualizarCliente(id, request);

            assertThat(result).isNotNull();
            assertThat(result.getPrimerNombre()).isEqualTo("Carlos");
            
            verify(repository).save(clienteCaptor.capture());
            Cliente saved = clienteCaptor.getValue();
            assertThat(saved.getPrimerNombre()).isEqualTo("Carlos");
            
            verify(eventPublisher).publish(eventCaptor.capture());
            ClienteActualizadoEvent event = eventCaptor.getValue();
            assertThat(event.camposModificados()).contains("primerNombre");
        }

        @Test
        @DisplayName("Debe actualizar email si no existe")
        void debeActualizarEmailSiNoExiste() {
            UUID id = UUID.randomUUID();
            
            Cliente clienteExistente = Cliente.builder()
                    .id(id)
                    .primerNombre("Juan")
                    .primerApellido("Perez")
                    .email(new Email("juan@email.com"))
                    .activo(true)
                    .build();

            Cliente clienteActualizado = Cliente.builder()
                    .id(id)
                    .primerNombre("Juan")
                    .primerApellido("Perez")
                    .email(new Email("nuevo@email.com"))
                    .activo(true)
                    .build();

            ActualizarClienteRequestDto request = ActualizarClienteRequestDto.builder()
                    .email("nuevo@email.com")
                    .build();

            when(repository.findById(id)).thenReturn(java.util.Optional.of(clienteExistente));
            when(repository.existsByEmailAndIdNot("nuevo@email.com", id)).thenReturn(false);
            when(repository.save(any(Cliente.class))).thenReturn(clienteActualizado);

            useCase.actualizarCliente(id, request);

            verify(repository).save(clienteCaptor.capture());
            Cliente saved = clienteCaptor.getValue();
            assertThat(saved.getEmail().valor()).isEqualTo("nuevo@email.com");
        }

        @Test
        @DisplayName("Debe lanzar excepción si email ya existe al actualizar")
        void debeLanzarExcepcionSiEmailYaExiste() {
            UUID id = UUID.randomUUID();
            
            Cliente clienteExistente = Cliente.builder()
                    .id(id)
                    .primerNombre("Juan")
                    .email(new Email("juan@email.com"))
                    .build();

            ActualizarClienteRequestDto request = ActualizarClienteRequestDto.builder()
                    .email("existente@email.com")
                    .build();

            when(repository.findById(id)).thenReturn(java.util.Optional.of(clienteExistente));
            when(repository.existsByEmailAndIdNot("existente@email.com", id)).thenReturn(true);

            org.junit.jupiter.api.Assertions.assertThrows(
                    ClienteYaExisteException.class,
                    () -> useCase.actualizarCliente(id, request)
            );
        }

        @Test
        @DisplayName("Debe actualizar múltiples campos")
        void debeActualizarMultiplesCampos() {
            UUID id = UUID.randomUUID();
            
            Cliente clienteExistente = Cliente.builder()
                    .id(id)
                    .primerNombre("Juan")
                    .primerApellido("Perez")
                    .email(new Email("juan@email.com"))
                    .telefono("3001234567")
                    .activo(true)
                    .build();

            ActualizarClienteRequestDto request = ActualizarClienteRequestDto.builder()
                    .primerNombre("Carlos")
                    .segundoNombre("Alberto")
                    .primerApellido("Lopez")
                    .telefono("3009999999")
                    .activo(false)
                    .build();

            when(repository.findById(id)).thenReturn(java.util.Optional.of(clienteExistente));
            when(repository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));
            when(mapper.toResponseDto(any(Cliente.class))).thenReturn(ClienteResponseDto.builder().build());

            useCase.actualizarCliente(id, request);

            verify(eventPublisher).publish(eventCaptor.capture());
            ClienteActualizadoEvent event = eventCaptor.getValue();
            assertThat(event.camposModificados()).contains("primerNombre", "segundoNombre", "primerApellido", "telefono", "activo");
        }

        @Test
        @DisplayName("Debe retornar cliente sin cambios si request vacío")
        void debeRetornarSinCambiosSiRequestVacio() {
            UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            
            Cliente clienteExistente = Cliente.builder()
                    .id(id)
                    .primerNombre("Juan")
                    .primerApellido("Perez")
                    .email(new Email("juan@email.com"))
                    .activo(true)
                    .build();

            ClienteResponseDto responseDto = ClienteResponseDto.builder()
                    .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                    .primerNombre("Juan")
                    .primerApellido("Perez")
                    .email("juan@perez.com")
                    .activo(true)
                    .build();

            ActualizarClienteRequestDto request = ActualizarClienteRequestDto.builder().build();

            when(repository.findById(id)).thenReturn(java.util.Optional.of(clienteExistente));
            when(mapper.toResponseDto(clienteExistente)).thenReturn(responseDto);

            ClienteResponseDto result = useCase.actualizarCliente(id, request);

            assertThat(result).isNotNull();
            verify(eventPublisher, org.mockito.Mockito.never()).publish(any(ClienteActualizadoEvent.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción si cliente no existe")
        void debeLanzarExcepcionSiNoExiste() {
            UUID id = UUID.randomUUID();
            
            when(repository.findById(id)).thenReturn(java.util.Optional.empty());

            org.junit.jupiter.api.Assertions.assertThrows(
                    ClienteNoEncontradoException.class,
                    () -> useCase.actualizarCliente(id, ActualizarClienteRequestDto.builder().build())
            );
        }
    }
}
