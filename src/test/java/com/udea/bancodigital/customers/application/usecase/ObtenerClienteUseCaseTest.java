package com.udea.bancodigital.customers.application.usecase;

import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.application.mapper.ClienteMapper;
import com.udea.bancodigital.customers.domain.exception.ClienteNoEncontradoException;
import com.udea.bancodigital.customers.domain.model.Cliente;
import com.udea.bancodigital.customers.domain.model.Email;
import com.udea.bancodigital.customers.domain.model.NumeroCedula;
import com.udea.bancodigital.customers.domain.port.out.ClienteRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ObtenerClienteUseCase")
public class ObtenerClienteUseCaseTest {
    @Mock
    private ClienteRepositoryPort clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ObtenerClienteUseCase useCase;

    private UUID id;
    private Cliente cliente;
    private ClienteResponseDto responseDto;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        cliente = Cliente.builder()
                .id(id)
                .numeroCedula(NumeroCedula.of("1234567890"))
                .primerNombre("María")
                .primerApellido("González")
                .email(Email.of("maria@test.com"))
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .activo(true)
                .build();

        responseDto = ClienteResponseDto.builder()
                .id(id)
                .numeroCedula("1234567890")
                .primerNombre("María")
                .primerApellido("González")
                .email("maria@test.com")
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("TC-01 Debe retornar cliente cuando el ID existe")
    void debeRetornarCliente_cuandoIdExiste() {
        // ── Arrange ──────────────────────────────────────────────────────────
        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toResponseDto(cliente)).thenReturn(responseDto);

        // ── Act ───────────────────────────────────────────────────────────────
        ClienteResponseDto resultado = useCase.ejecutar(id);

        // ── Assert ────────────────────────────────────────────────────────────
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getEmail()).isEqualTo("maria@test.com");

        verify(clienteRepository, times(1)).findById(id);
        verify(clienteMapper, times(1)).toResponseDto(cliente);
    }

    @Test
    @DisplayName("TC-02 Debe lanzar ClienteNoEncontradoException cuando el ID no existe")
    void debeLanzarExcepcion_cuandoClienteNoExiste() {
        // ── Arrange ──────────────────────────────────────────────────────────
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        // ── Act & Assert ──────────────────────────────────────────────────────
        assertThatThrownBy(() -> useCase.ejecutar(id))
                .isInstanceOf(ClienteNoEncontradoException.class);

        verify(clienteRepository, times(1)).findById(id);
        verify(clienteMapper, never()).toResponseDto(any());
    }
}
