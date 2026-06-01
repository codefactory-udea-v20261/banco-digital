package com.udea.bancodigital.accounts.application.usecase;

import com.udea.bancodigital.accounts.application.dto.CuentaResponseDto;
import com.udea.bancodigital.accounts.application.mapper.CuentaMapper;
import com.udea.bancodigital.accounts.domain.exception.ClienteNoEncontradoException;
import com.udea.bancodigital.accounts.domain.model.Cuenta;
import com.udea.bancodigital.accounts.domain.model.EstadoCuenta;
import com.udea.bancodigital.accounts.domain.model.TipoCuenta;
import com.udea.bancodigital.accounts.domain.port.out.ClienteServicePort;
import com.udea.bancodigital.accounts.domain.port.out.CuentaRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarCuentasClienteUseCaseTest {

    @Mock
    private CuentaRepositoryPort cuentaRepository;

    @Mock
    private ClienteServicePort clienteService;

    @Mock
    private CuentaMapper cuentaMapper;

    @InjectMocks
    private ListarCuentasClienteUseCase useCase;

    @Test
    void deberiaRetornarCuentasDelCliente() {
        UUID clienteId = UUID.randomUUID();
        Cuenta cuenta = Cuenta.builder()
                .id(UUID.randomUUID())
                .clienteId(clienteId)
                .numeroCuenta("CTA-1234567890")
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldo(new BigDecimal("100000"))
                .estado(EstadoCuenta.ACTIVA)
                .fechaApertura(LocalDate.now())
                .build();
        CuentaResponseDto dto = CuentaResponseDto.builder()
                .id(cuenta.getId())
                .clienteId(clienteId)
                .numeroCuenta("CTA-1234567890")
                .build();

        when(clienteService.existeCliente(clienteId)).thenReturn(true);
        when(cuentaRepository.findAllByClienteId(clienteId)).thenReturn(List.of(cuenta));
        when(cuentaMapper.toResponseDto(cuenta)).thenReturn(dto);

        List<CuentaResponseDto> result = useCase.listarPorClienteId(clienteId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNumeroCuenta()).isEqualTo("CTA-1234567890");
        verify(cuentaRepository).findAllByClienteId(clienteId);
    }

    @Test
    void deberiaRetornarListaVaciaCuandoClienteNoTieneCuentas() {
        UUID clienteId = UUID.randomUUID();

        when(clienteService.existeCliente(clienteId)).thenReturn(true);
        when(cuentaRepository.findAllByClienteId(clienteId)).thenReturn(List.of());

        List<CuentaResponseDto> result = useCase.listarPorClienteId(clienteId);

        assertThat(result).isEmpty();
    }

    @Test
    void deberiaLanzarExcepcionCuandoClienteNoExiste() {
        UUID clienteId = UUID.randomUUID();

        when(clienteService.existeCliente(clienteId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.listarPorClienteId(clienteId))
                .isInstanceOf(ClienteNoEncontradoException.class);
    }
}
