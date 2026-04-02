package com.udea.bancodigital.customers.application.usecase;

import com.udea.bancodigital.customers.application.dto.ActualizarClienteRequestDto;
import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.application.mapper.ClienteMapper;
import com.udea.bancodigital.customers.domain.exception.ClienteYaExisteException;
import com.udea.bancodigital.customers.domain.exception.ClienteNoEncontradoException;
import com.udea.bancodigital.customers.domain.model.Cliente;
import com.udea.bancodigital.customers.domain.model.Email;
import com.udea.bancodigital.customers.domain.model.NumeroCedula;
import com.udea.bancodigital.customers.domain.port.out.ClienteRepositoryPort;
import com.udea.bancodigital.customers.domain.port.out.DomainEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActualizarClienteUseCaseTest {

    @Mock
    private ClienteRepositoryPort repository; // Necesitamos el mock del puerto de salida

    @Mock
    private ClienteMapper mapper;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private ActualizarClienteUseCase useCase;

    @Test
    void deberiaActualizarClienteExitosamente_cuandoClienteExiste() {
        // 1. GIVEN (Preparación)
        UUID id = UUID.randomUUID();

        // Cliente que ya está en la "base de datos"
        Cliente clienteExistente = Cliente.builder()
                .id(id)
                .numeroCedula(NumeroCedula.of("1234567"))
                .primerNombre("Juan")
                .primerApellido("Perez")
                .email(Email.of("juan@old.com"))
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .activo(true)
                .build();

        // Datos que vienen en la petición (Solo queremos cambiar el nombre y email)
        ActualizarClienteRequestDto request = ActualizarClienteRequestDto.builder()
                .primerNombre("Carlos")
                .email("carlos@new.com")
                .build();

        // Configuramos los mocks
        when(repository.findById(id)).thenReturn(Optional.of(clienteExistente));
        when(repository.existsByEmailAndIdNot("carlos@new.com", id)).thenReturn(false);
        when(repository.save(any(Cliente.class))).thenAnswer(i -> i.getArguments()[0]);
        when(mapper.toResponseDto(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente cliente = invocation.getArgument(0);
            return ClienteResponseDto.builder()
                    .id(cliente.getId())
                    .numeroCedula(cliente.getNumeroCedula().valor())
                    .primerNombre(cliente.getPrimerNombre())
                    .primerApellido(cliente.getPrimerApellido())
                    .email(cliente.getEmail().valor())
                    .fechaNacimiento(cliente.getFechaNacimiento())
                    .activo(cliente.isActivo())
                    .build();
        });

        // 2. WHEN (Ejecución)
        ClienteResponseDto resultado = useCase.actualizarCliente(id, request);

        // 3. THEN (Verificación)
        assertNotNull(resultado);
        assertEquals("Carlos", resultado.getPrimerNombre()); // Cambió
        assertEquals("carlos@new.com", resultado.getEmail()); // Cambió
        assertEquals("1234567", resultado.getNumeroCedula()); // Se mantuvo (Inmutable)

        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(any(Cliente.class));
        verify(eventPublisher, times(1)).publish(any());
    }

    @Test
    void deberiaLanzarException_cuandoClienteNoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();
        ActualizarClienteRequestDto request = ActualizarClienteRequestDto.builder().build();

        when(repository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        ClienteNoEncontradoException exception = assertThrows(ClienteNoEncontradoException.class, () ->
                useCase.actualizarCliente(id, request)
        );

        assertEquals("No se encontró un cliente con ID: " + id, exception.getMessage());
    }

    @Test
    void deberiaLanzarException_cuandoEmailPerteneceAOtroCliente() {
        UUID id = UUID.randomUUID();

        Cliente clienteExistente = Cliente.builder()
                .id(id)
                .numeroCedula(NumeroCedula.of("1234567"))
                .primerNombre("Juan")
                .primerApellido("Perez")
                .email(Email.of("juan@old.com"))
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .activo(true)
                .build();

        ActualizarClienteRequestDto request = ActualizarClienteRequestDto.builder()
                .email("repetido@test.com")
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(clienteExistente));
        when(repository.existsByEmailAndIdNot("repetido@test.com", id)).thenReturn(true);

        ClienteYaExisteException exception = assertThrows(ClienteYaExisteException.class, () ->
                useCase.actualizarCliente(id, request)
        );

        assertTrue(exception.getMessage().contains("repetido@test.com"));
        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }
}
