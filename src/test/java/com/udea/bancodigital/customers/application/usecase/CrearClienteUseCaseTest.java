package com.udea.bancodigital.customers.application.usecase;

import com.udea.bancodigital.customers.application.dto.ClienteResponseDto;
import com.udea.bancodigital.customers.application.dto.CrearClienteRequestDto;
import com.udea.bancodigital.customers.application.mapper.ClienteMapper;
import com.udea.bancodigital.customers.domain.exception.ClienteYaExisteException;
import com.udea.bancodigital.customers.domain.model.Cliente;
import com.udea.bancodigital.customers.domain.model.Email;
import com.udea.bancodigital.customers.domain.model.NumeroCedula;
import com.udea.bancodigital.customers.domain.port.out.ClienteAccessProvisioningPort;
import com.udea.bancodigital.customers.domain.port.out.ClienteRepositoryPort;
import com.udea.bancodigital.customers.domain.port.out.DomainEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @InjectMocks
    private CrearClienteUseCase useCase;

    private CrearClienteRequestDto requestValido;
    private Cliente clienteDomain;
    private ClienteResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestValido = CrearClienteRequestDto.builder()
                .numeroCedula("1234567890")
                .primerNombre("María")
                .primerApellido("González")
                .email("maria.gonzalez@test.com")
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .build();
                
        clienteDomain = Cliente.builder()
                .id(UUID.randomUUID())
                .numeroCedula(NumeroCedula.of("1234567890"))
                .primerNombre("María")
                .primerApellido("González")
                .email(Email.of("maria.gonzalez@test.com"))
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .activo(true)
                .build();
                
        responseDto = ClienteResponseDto.builder()
                .id(clienteDomain.getId())
                .numeroCedula("1234567890")
                .primerNombre("María")
                .primerApellido("González")
                .email("maria.gonzalez@test.com")
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("TC-01 ✅ Debe registrar cliente cuando los datos son válidos y únicos")
    void debeRegistrarCliente_cuandoDatosValidosYUnicos() {
        // ── Arrange ──────────────────────────────────────────────────────────
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(clienteRepository.existsByCedula(anyString())).thenReturn(false);
        when(accessProvisioningPort.existsByEmail(anyString())).thenReturn(false);
        when(clienteMapper.toDomain(any(CrearClienteRequestDto.class))).thenReturn(clienteDomain);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteDomain);
        when(clienteMapper.toResponseDto(any(Cliente.class))).thenReturn(responseDto);

        // ── Act ───────────────────────────────────────────────────────────────
        ClienteResponseDto resultado = useCase.ejecutar(requestValido);

        // ── Assert ────────────────────────────────────────────────────────────
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("maria.gonzalez@test.com");
        verify(clienteRepository, times(1)).save(any(Cliente.class));
        verify(accessProvisioningPort, times(1))
                .provisionAccess(clienteDomain.getId(), "maria.gonzalez@test.com");
        verify(eventPublisher, times(1)).publish(any());
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

    @Test
    @DisplayName("TC-04 ❌ Debe lanzar ClienteYaExisteException cuando el email ya existe en auth")
    void debeLanzarExcepcion_cuandoEmailYaExisteComoUsuario() {
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(clienteRepository.existsByCedula(anyString())).thenReturn(false);
        when(accessProvisioningPort.existsByEmail("maria.gonzalez@test.com")).thenReturn(true);

        assertThatThrownBy(() -> useCase.ejecutar(requestValido))
                .isInstanceOf(ClienteYaExisteException.class)
                .hasMessageContaining("email");

        verify(clienteRepository, never()).save(any());
        verify(accessProvisioningPort, never()).provisionAccess(any(), anyString());
    }
}
