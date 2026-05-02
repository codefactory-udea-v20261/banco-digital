package com.udea.bancodigital.customers.application.usecase;

import com.udea.bancodigital.customers.application.dto.CrearClienteRequestDto;
import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.application.mapper.ClienteMapper;
import com.udea.bancodigital.customers.domain.event.ClienteRegistradoEvent;
import com.udea.bancodigital.customers.domain.model.Cliente;
import com.udea.bancodigital.customers.domain.model.Email;
import com.udea.bancodigital.customers.domain.model.NumeroCedula;
import com.udea.bancodigital.customers.domain.port.out.ClienteRepositoryPort;
import com.udea.bancodigital.customers.domain.port.out.ClienteAccessProvisioningPort;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CrearClienteUseCase")
class CrearClienteUseCaseTest {

    @Mock
    private ClienteRepositoryPort clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @Mock
    private ClienteAccessProvisioningPort accessProvisioningPort;

    @Mock
    private DomainEventPublisher eventPublisher;

    private CrearClienteUseCase useCase;

    @Captor
    private ArgumentCaptor<Cliente> clienteCaptor;

    @Captor
    private ArgumentCaptor<ClienteRegistradoEvent> eventCaptor;

    @BeforeEach
    void setUp() {
        useCase = new CrearClienteUseCase(clienteRepository, clienteMapper, accessProvisioningPort, eventPublisher);
    }

    @Nested
    @DisplayName("Crear cliente")
    class CrearClienteTest {

        @Test
        @DisplayName("Debe crear cliente exitosamente")
        void debeCrearClienteExitosamente() {
            // Given
            CrearClienteRequestDto request = CrearClienteRequestDto.builder()
                    .primerNombre("Juan")
                    .primerApellido("Perez")
                    .email("juan.perez@email.com")
                    .numeroCedula("12345678")
                    .telefono("3001234567")
                    .build();

            UUID clienteId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

            Cliente cliente = Cliente.builder()
                    .id(clienteId)
                    .numeroCedula(new NumeroCedula("12345678"))
                    .primerNombre("Juan")
                    .primerApellido("Perez")
                    .email(new Email("juan.perez@email.com"))
                    .telefono("3001234567")
                    .activo(true)
                    .build();

            Cliente clienteGuardado = Cliente.builder()
                    .id(clienteId)
                    .numeroCedula(new NumeroCedula("12345678"))
                    .primerNombre("Juan")
                    .primerApellido("Perez")
                    .email(new Email("juan.perez@email.com"))
                    .telefono("3001234567")
                    .activo(true)
                    .build();

            ClienteResponseDto responseDto = ClienteResponseDto.builder()
                    .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                    .primerNombre("Juan")
                    .primerApellido("Perez")
                    .email("juan.perez@email.com")
                    .build();

            when(clienteRepository.existsByEmail("juan.perez@email.com")).thenReturn(false);
            when(clienteRepository.existsByCedula("12345678")).thenReturn(false);
            when(accessProvisioningPort.existsByEmail("juan.perez@email.com")).thenReturn(false);
            when(clienteMapper.toDomain(request)).thenReturn(cliente);
            when(clienteRepository.save(cliente)).thenReturn(clienteGuardado);
            when(clienteMapper.toResponseDto(clienteGuardado)).thenReturn(responseDto);

            // When
            ClienteResponseDto result = useCase.crearCliente(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getPrimerNombre()).isEqualTo("Juan");
            assertThat(result.getEmail()).isEqualTo("juan.perez@email.com");

            // Verify interactions
            verify(clienteRepository).existsByEmail("juan.perez@email.com");
            verify(clienteRepository).existsByCedula("12345678");
            verify(accessProvisioningPort).existsByEmail("juan.perez@email.com");
            verify(clienteRepository).save(any(Cliente.class));
            verify(accessProvisioningPort).provisionAccess(any(UUID.class), any(String.class));
            verify(eventPublisher).publish(any(ClienteRegistradoEvent.class));
        }

        @Test
        @DisplayName("Debe lanzar excepcion si email ya existe en repositorio")
        void debeLanzarExcepcionSiEmailYaExisteRepositorio() {
            CrearClienteRequestDto request = CrearClienteRequestDto.builder()
                    .email("existente@email.com")
                    .numeroCedula("12345678")
                    .build();

            when(clienteRepository.existsByEmail("existente@email.com")).thenReturn(true);

            org.junit.jupiter.api.Assertions.assertThrows(
                    com.udea.bancodigital.customers.domain.exception.ClienteYaExisteException.class,
                    () -> useCase.crearCliente(request)
            );
        }

        @Test
        @DisplayName("Debe lanzar excepcion si cedula ya existe")
        void debeLanzarExcepcionSiCedulaYaExiste() {
            CrearClienteRequestDto request = CrearClienteRequestDto.builder()
                    .email("nuevo@email.com")
                    .numeroCedula("12345678")
                    .build();

            when(clienteRepository.existsByEmail("nuevo@email.com")).thenReturn(false);
            when(clienteRepository.existsByCedula("12345678")).thenReturn(true);

            org.junit.jupiter.api.Assertions.assertThrows(
                    com.udea.bancodigital.customers.domain.exception.ClienteYaExisteException.class,
                    () -> useCase.crearCliente(request)
            );
        }

        @Test
        @DisplayName("Debe lanzar excepcion si email ya existe en acceso")
        void debeLanzarExcepcionSiEmailYaExisteAcceso() {
            CrearClienteRequestDto request = CrearClienteRequestDto.builder()
                    .email("existente@email.com")
                    .numeroCedula("12345678")
                    .build();

            when(clienteRepository.existsByEmail("existente@email.com")).thenReturn(false);
            when(clienteRepository.existsByCedula("12345678")).thenReturn(false);
            when(accessProvisioningPort.existsByEmail("existente@email.com")).thenReturn(true);

            org.junit.jupiter.api.Assertions.assertThrows(
                    com.udea.bancodigital.customers.domain.exception.ClienteYaExisteException.class,
                    () -> useCase.crearCliente(request)
            );
        }

        @Test
        @DisplayName("Debe publicar evento con nombre completo")
        void debePublicarEventoConNombreCompleto() {
            CrearClienteRequestDto request = CrearClienteRequestDto.builder()
                    .primerNombre("Juan")
                    .segundoNombre("Carlos")
                    .primerApellido("Perez")
                    .segundoApellido("Lopez")
                    .email("juan@email.com")
                    .numeroCedula("12345678")
                    .build();

            UUID clienteId = UUID.randomUUID();

            Cliente cliente = Cliente.builder()
                    .id(clienteId)
                    .numeroCedula(new NumeroCedula("12345678"))
                    .primerNombre("Juan")
                    .segundoNombre("Carlos")
                    .primerApellido("Perez")
                    .segundoApellido("Lopez")
                    .email(new Email("juan@email.com"))
                    .activo(true)
                    .build();

            Cliente clienteGuardado = Cliente.builder()
                    .id(clienteId)
                    .numeroCedula(new NumeroCedula("12345678"))
                    .primerNombre("Juan")
                    .segundoNombre("Carlos")
                    .primerApellido("Perez")
                    .segundoApellido("Lopez")
                    .email(new Email("juan@email.com"))
                    .activo(true)
                    .build();

            ClienteResponseDto responseDto = ClienteResponseDto.builder()
                    .id(clienteId)
                    .build();

            when(clienteRepository.existsByEmail("juan@email.com")).thenReturn(false);
            when(clienteRepository.existsByCedula("12345678")).thenReturn(false);
            when(accessProvisioningPort.existsByEmail("juan@email.com")).thenReturn(false);
            when(clienteMapper.toDomain(request)).thenReturn(cliente);
            when(clienteRepository.save(cliente)).thenReturn(clienteGuardado);
            when(clienteMapper.toResponseDto(clienteGuardado)).thenReturn(responseDto);

            useCase.crearCliente(request);

            verify(eventPublisher).publish(eventCaptor.capture());
            ClienteRegistradoEvent event = eventCaptor.getValue();
            assertThat(event.nombreCompleto()).isEqualTo("Juan Perez");
        }
    }
}
