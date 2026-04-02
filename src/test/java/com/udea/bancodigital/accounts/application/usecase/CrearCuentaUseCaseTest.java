package com.udea.bancodigital.accounts.application.usecase;

import com.udea.bancodigital.accounts.application.dto.CrearCuentaRequestDto;
import com.udea.bancodigital.accounts.domain.exception.ClienteInactivoException;
import com.udea.bancodigital.accounts.domain.exception.ClienteNoEncontradoException;
import com.udea.bancodigital.accounts.domain.exception.TipoCuentaInvalidoException;
import com.udea.bancodigital.accounts.domain.model.Cuenta;
import com.udea.bancodigital.accounts.domain.model.TipoCuenta;
import com.udea.bancodigital.accounts.domain.port.out.ClienteServicePort;
import com.udea.bancodigital.accounts.domain.port.out.CuentaRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CrearCuentaUseCase")
class CrearCuentaUseCaseTest {

    @Mock
    private CuentaRepositoryPort cuentaRepository;

    @Mock
    private ClienteServicePort clienteService;

    @InjectMocks
    private CrearCuentaUseCase useCase;

    @Test
    @DisplayName("Debe crear cuenta cuando el cliente existe y está activo")
    void debeCrearCuentaCuandoClienteExisteYEstaActivo() {
        UUID clienteId = UUID.randomUUID();
        CrearCuentaRequestDto request = CrearCuentaRequestDto.builder()
                .clienteId(clienteId)
                .tipoCuenta("AHORRO")
                .build();

        when(clienteService.existeCliente(clienteId)).thenReturn(true);
        when(clienteService.isClienteActivo(clienteId)).thenReturn(true);
        when(cuentaRepository.existsByNumeroCuenta(anyString())).thenReturn(false);
        when(cuentaRepository.save(any(Cuenta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cuenta cuenta = useCase.crearCuenta(request);

        assertThat(cuenta.getClienteId()).isEqualTo(clienteId);
        assertThat(cuenta.getTipoCuenta()).isEqualTo(TipoCuenta.AHORRO);
        assertThat(cuenta.getNumeroCuenta()).startsWith("CTA-");

        ArgumentCaptor<Cuenta> cuentaCaptor = ArgumentCaptor.forClass(Cuenta.class);
        verify(cuentaRepository).save(cuentaCaptor.capture());
        assertThat(cuentaCaptor.getValue().getEstado().name()).isEqualTo("ACTIVA");
    }

    @Test
    @DisplayName("Debe rechazar la creación cuando el cliente no existe")
    void debeRechazarCreacionCuandoClienteNoExiste() {
        UUID clienteId = UUID.randomUUID();
        CrearCuentaRequestDto request = CrearCuentaRequestDto.builder()
                .clienteId(clienteId)
                .tipoCuenta("AHORRO")
                .build();

        when(clienteService.existeCliente(clienteId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.crearCuenta(request))
                .isInstanceOf(ClienteNoEncontradoException.class);
    }

    @Test
    @DisplayName("Debe rechazar la creación cuando el cliente está inactivo")
    void debeRechazarCreacionCuandoClienteEstaInactivo() {
        UUID clienteId = UUID.randomUUID();
        CrearCuentaRequestDto request = CrearCuentaRequestDto.builder()
                .clienteId(clienteId)
                .tipoCuenta("AHORRO")
                .build();

        when(clienteService.existeCliente(clienteId)).thenReturn(true);
        when(clienteService.isClienteActivo(clienteId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.crearCuenta(request))
                .isInstanceOf(ClienteInactivoException.class);
    }

    @Test
    @DisplayName("Debe rechazar tipo de cuenta inválido")
    void debeRechazarTipoCuentaInvalido() {
        UUID clienteId = UUID.randomUUID();
        CrearCuentaRequestDto request = CrearCuentaRequestDto.builder()
                .clienteId(clienteId)
                .tipoCuenta("FIDUCUENTA")
                .build();

        when(clienteService.existeCliente(clienteId)).thenReturn(true);
        when(clienteService.isClienteActivo(clienteId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.crearCuenta(request))
                .isInstanceOf(TipoCuentaInvalidoException.class);
    }

    @Test
    @DisplayName("Debe regenerar el número de cuenta si encuentra una colisión")
    void debeRegenerarNumeroCuentaSiEncuentraColision() {
        UUID clienteId = UUID.randomUUID();
        CrearCuentaRequestDto request = CrearCuentaRequestDto.builder()
                .clienteId(clienteId)
                .tipoCuenta("CORRIENTE")
                .build();

        when(clienteService.existeCliente(clienteId)).thenReturn(true);
        when(clienteService.isClienteActivo(clienteId)).thenReturn(true);
        when(cuentaRepository.existsByNumeroCuenta(anyString())).thenReturn(true, false);
        when(cuentaRepository.save(any(Cuenta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cuenta cuenta = useCase.crearCuenta(request);

        assertThat(cuenta.getNumeroCuenta()).startsWith("CTA-");
        verify(cuentaRepository, times(2)).existsByNumeroCuenta(anyString());
    }
}
