package com.udea.bancodigital.customers.application.usecase;

import com.udea.bancodigital.customers.application.dto.ActualizarClienteRequestDto;
import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.application.mapper.ClienteMapper;
import com.udea.bancodigital.customers.domain.exception.ClienteNoEncontradoException;
import com.udea.bancodigital.customers.domain.model.Cliente;
import com.udea.bancodigital.customers.domain.model.Email;
import com.udea.bancodigital.customers.domain.model.NumeroCedula;
import com.udea.bancodigital.customers.domain.port.out.ClienteRepositoryPort;
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
}
