package com.udea.bancodigital.transactions.application.usecase;

import com.udea.bancodigital.transactions.application.dto.HistorialTransaccionDto;
import com.udea.bancodigital.transactions.application.mapper.HistorialTransaccionMapper;
import com.udea.bancodigital.transactions.domain.model.Transaccion;
import com.udea.bancodigital.transactions.domain.port.out.TransaccionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarHistorialUseCaseTest {

    @Mock
    private TransaccionRepositoryPort transaccionRepository;

    @Mock
    private HistorialTransaccionMapper mapper;

    @InjectMocks
    private ConsultarHistorialUseCase useCase;

    @Test
    void shouldReturnHistorial() {
        UUID cuentaId = UUID.randomUUID();
        Transaccion transaccion = Transaccion.builder().id(UUID.randomUUID()).build();
        HistorialTransaccionDto dto = HistorialTransaccionDto.builder().tipo("RETIRO").build();

        when(transaccionRepository.findByCuentaIdOrderByFechaDesc(cuentaId)).thenReturn(List.of(transaccion));
        when(mapper.toDto(transaccion)).thenReturn(dto);

        List<HistorialTransaccionDto> result = useCase.ejecutar(cuentaId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTipo()).isEqualTo("RETIRO");
    }
}
