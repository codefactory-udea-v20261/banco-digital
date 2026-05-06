package com.udea.bancodigital.transactions.application.mapper;

import com.udea.bancodigital.transactions.application.dto.HistorialTransaccionDto;
import com.udea.bancodigital.transactions.domain.enums.TipoTransaccion;
import com.udea.bancodigital.transactions.domain.model.Transaccion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HistorialTransaccionMapper")
class HistorialTransaccionMapperTest {
    private HistorialTransaccionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new HistorialTransaccionMapper();
    }

    @Nested
    @DisplayName("toDto()")
    class ToDtoTest {

        @Test
        @DisplayName("Debe mapear RETIRO con descripción y monto correctos")
        void debeMappearRetiro() {
            Transaccion t = buildTransaccion(TipoTransaccion.RETIRO.getId(), new BigDecimal("500.00"));
            HistorialTransaccionDto dto = mapper.toDto(t);

            assertThat(dto.getTipo()).isEqualTo("Retiro de efectivo");
            assertThat(dto.getMonto()).isEqualByComparingTo(new BigDecimal("500.00"));
        }

        @Test
        @DisplayName("Debe mapear DEPOSITO correctamente")
        void debeMappearDeposito() {
            Transaccion t = buildTransaccion(TipoTransaccion.DEPOSITO.getId(), new BigDecimal("1000.00"));
            assertThat(mapper.toDto(t).getTipo()).isEqualTo("Depósito de efectivo");
        }

        @Test
        @DisplayName("Debe mapear TRANSFERENCIA_ENVIADA correctamente")
        void debeMappearTransferenciaEnviada() {
            Transaccion t = buildTransaccion(TipoTransaccion.TRANSFERENCIA_ENVIADA.getId(), new BigDecimal("200.00"));
            assertThat(mapper.toDto(t).getTipo()).isEqualTo("Transferencia enviada");
        }

        @Test
        @DisplayName("Debe mapear TRANSFERENCIA_RECIBIDA correctamente")
        void debeMappearTransferenciaRecibida() {
            Transaccion t = buildTransaccion(TipoTransaccion.TRANSFERENCIA_RECIBIDA.getId(), new BigDecimal("300.00"));
            assertThat(mapper.toDto(t).getTipo()).isEqualTo("Transferencia recibida");
        }

        @Test
        @DisplayName("Debe usar 'Desconocido' para tipoId null")
        void debeUsarDesconocidoCuandoTipoIdNull() {
            Transaccion t = Transaccion.builder()
                    .tipoId(null)
                    .monto(new BigDecimal("100.00"))
                    .createdAt(OffsetDateTime.now())
                    .build();

            assertThat(mapper.toDto(t).getTipo()).isEqualTo("Desconocido");
        }

        @Test
        @DisplayName("Debe usar 'Desconocido' para tipoId inexistente")
        void debeUsarDesconocidoCuandoTipoIdInexistente() {
            Transaccion t = buildTransaccion((short) 99, new BigDecimal("100.00"));
            assertThat(mapper.toDto(t).getTipo()).isEqualTo("Desconocido");
        }

        @Test
        @DisplayName("Debe preservar la fechaHora exacta de la transacción")
        void debePreservarFechaHora() {
            OffsetDateTime ahora = OffsetDateTime.now();
            Transaccion t = Transaccion.builder()
                    .tipoId(TipoTransaccion.RETIRO.getId())
                    .monto(new BigDecimal("100.00"))
                    .createdAt(ahora)
                    .build();

            assertThat(mapper.toDto(t).getFechaHora()).isEqualTo(ahora);
        }

        private Transaccion buildTransaccion(short tipoId, BigDecimal monto) {
            return Transaccion.builder()
                    .id(UUID.randomUUID())
                    .tipoId(tipoId)
                    .monto(monto)
                    .createdAt(OffsetDateTime.now())
                    .build();
        }
    }

}
