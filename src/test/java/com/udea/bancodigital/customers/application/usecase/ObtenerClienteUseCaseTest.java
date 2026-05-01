package com.udea.bancodigital.customers.application.usecase;

import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.application.mapper.ClienteMapper;
import com.udea.bancodigital.customers.domain.exception.ClienteNoEncontradoException;
import com.udea.bancodigital.customers.domain.model.Cliente;
import com.udea.bancodigital.customers.domain.port.out.ClienteRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ObtenerClienteUseCase")
class ObtenerClienteUseCaseTest {

    @Mock
    private ClienteRepositoryPort clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    private ObtenerClienteUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ObtenerClienteUseCase(clienteRepository, clienteMapper);
    }

    @Nested
    @DisplayName("Obtener por ID")
    class ObtenerPorIdTest {

        @Test
        @DisplayName("Debe retornar cliente cuando existe")
        void debeRetornarClienteCuandoExiste() {
            UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            
            Cliente cliente = Cliente.builder()
                    .id(id)
                    .primerNombre("Juan")
                    .primerApellido("Perez")
                    .email(new com.udea.bancodigital.customers.domain.model.Email("juan@email.com"))
                    .activo(true)
                    .build();

            ClienteResponseDto responseDto = ClienteResponseDto.builder()
                    .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                    .primerNombre("Juan")
                    .primerApellido("Perez")
                    .email("juan@email.com")
                    .activo(true)
                    .build();

            when(clienteRepository.findById(id)).thenReturn(java.util.Optional.of(cliente));
            when(clienteMapper.toResponseDto(cliente)).thenReturn(responseDto);

            ClienteResponseDto result = useCase.obtenerPorId(id);

            assertThat(result).isNotNull();
            assertThat(result.getPrimerNombre()).isEqualTo("Juan");
            assertThat(result.getEmail()).isEqualTo("juan@email.com");
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando cliente no existe")
        void debeLanzarExcepcionCuandoNoExiste() {
            UUID id = UUID.randomUUID();
            
            when(clienteRepository.findById(id)).thenReturn(java.util.Optional.empty());

            org.junit.jupiter.api.Assertions.assertThrows(
                    ClienteNoEncontradoException.class,
                    () -> useCase.obtenerPorId(id)
            );
        }
    }

    @Nested
    @DisplayName("Ejecutar")
    class EjecutarTest {

        @Test
        @DisplayName("Debe retornar cliente cuando ejecuta con ID válido")
        void debeRetornarClienteCuandoEjecuta() {
            UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            
            Cliente cliente = Cliente.builder()
                    .id(id)
                    .primerNombre("María")
                    .primerApellido("López")
                    .email(new com.udea.bancodigital.customers.domain.model.Email("maria@email.com"))
                    .activo(true)
                    .build();

            ClienteResponseDto responseDto = ClienteResponseDto.builder()
                    .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                    .primerNombre("María")
                    .primerApellido("López")
                    .email("maria@email.com")
                    .build();

            when(clienteRepository.findById(id)).thenReturn(java.util.Optional.of(cliente));
            when(clienteMapper.toResponseDto(cliente)).thenReturn(responseDto);

            ClienteResponseDto result = useCase.ejecutar(id);

            assertThat(result).isNotNull();
            assertThat(result.getPrimerNombre()).isEqualTo("María");
        }
    }
}
