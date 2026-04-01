package com.udea.bancodigital.customers.application.usecase;

import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.application.dto.CrearClienteRequestDto;
import com.udea.bancodigital.customers.domain.exception.ClienteYaExisteException;
import com.udea.bancodigital.customers.domain.port.out.ClienteRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias: Registro de nuevos clientes
 * Patrón: AAA (Arrange / Act / Assert)
 *
 * Escenarios cubiertos:
 *   TC-01: Registro exitoso con datos válidos
 *   TC-02: Rechazo por email duplicado
 *   TC-03: Rechazo por cédula duplicada
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("HU1 — CrearClienteUseCase")
class CrearClienteUseCaseTest {

    @Mock
    private ClienteRepositoryPort clienteRepository;

    @InjectMocks
    private CrearClienteUseCase useCase;

    private CrearClienteRequestDto requestValido;

    @BeforeEach
    void setUp() {
        requestValido = CrearClienteRequestDto.builder()
                .numeroCedula("1234567890")
                .primerNombre("María")
                .primerApellido("González")
                .email("maria.gonzalez@test.com")
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .build();
    }

    @Test
    @DisplayName("TC-01 ✅ Debe registrar cliente cuando los datos son válidos y únicos")
    void debeRegistrarCliente_cuandoDatosValidosYUnicos() {
        // ── Arrange ──────────────────────────────────────────────────────────
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(clienteRepository.existsByCedula(anyString())).thenReturn(false);
        // TODO Sprint 1: when(clienteRepository.save(any())).thenReturn(clienteMock);

        // ── Act ───────────────────────────────────────────────────────────────
        // TODO Sprint 1: ClienteResponseDto resultado = useCase.ejecutar(requestValido);

        // ── Assert ────────────────────────────────────────────────────────────
        // TODO Sprint 1:
        // assertThat(resultado).isNotNull();
        // assertThat(resultado.getEmail()).isEqualTo("maria.gonzalez@test.com");
        // verify(clienteRepository, times(1)).save(any());

        // Placeholder: verifica que las validaciones de unicidad se llaman
        verify(clienteRepository, atLeastOnce()).existsByEmail("maria.gonzalez@test.com");
    }

    @Test
    @DisplayName("TC-02 ❌ Debe lanzar ClienteYaExisteException cuando el email ya está registrado")
    void debeLanzarExcepcion_cuandoEmailYaExiste() {
        // ── Arrange ──────────────────────────────────────────────────────────
        when(clienteRepository.existsByEmail("maria.gonzalez@test.com")).thenReturn(true);

        // ── Act & Assert ──────────────────────────────────────────────────────
        assertThatThrownBy(() -> useCase.ejecutar(requestValido))
                .isInstanceOf(ClienteYaExisteException.class)
                .hasMessageContaining("maria.gonzalez@test.com");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC-03 ❌ Debe lanzar ClienteYaExisteException cuando la cédula ya está registrada")
    void debeLanzarExcepcion_cuandoCedulaYaExiste() {
        // ── Arrange ──────────────────────────────────────────────────────────
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(clienteRepository.existsByCedula("1234567890")).thenReturn(true);

        // ── Act & Assert ──────────────────────────────────────────────────────
        assertThatThrownBy(() -> useCase.ejecutar(requestValido))
                .isInstanceOf(ClienteYaExisteException.class)
                .hasMessageContaining("cédula");

        verify(clienteRepository, never()).save(any());
    }
}
