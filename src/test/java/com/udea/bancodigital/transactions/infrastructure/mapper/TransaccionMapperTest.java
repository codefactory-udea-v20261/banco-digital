package com.udea.bancodigital.transactions.infrastructure.mapper;

import com.udea.bancodigital.transactions.domain.enums.EstadoTransaccion;
import com.udea.bancodigital.transactions.domain.model.Transaccion;
import com.udea.bancodigital.transactions.infrastructure.entity.TransaccionEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TransaccionMapper")
class TransaccionMapperTest {

    private final TransaccionMapper mapper = new TransaccionMapper();

    @Nested
    @DisplayName("toEntity")
    class ToEntityTest {

        @Test
        @DisplayName("Debe convertir Transaccion a TransaccionEntity correctamente")
        void debeConvertirDomainAEntity() {
            UUID id = UUID.randomUUID();
            UUID cuentaOrigenId = UUID.randomUUID();
            UUID cuentaDestinoId = UUID.randomUUID();
            OffsetDateTime createdAt = OffsetDateTime.now();

            Transaccion domain = Transaccion.builder()
                    .id(id)
                    .cuentaOrigenId(cuentaOrigenId)
                    .cuentaDestinoId(cuentaDestinoId)
                    .tipoId((short) 1)
                    .monto(new BigDecimal("1500.50"))
                    .saldoAnterior(new BigDecimal("5000.00"))
                    .saldoPosterior(new BigDecimal("6500.50"))
                    .descripcion("Transferencia a cuenta de ahorros")
                    .referencia("REF-001")
                    .estado(EstadoTransaccion.COMPLETADA)
                    .createdAt(createdAt)
                    .createdBy("user@banco.com")
                    .build();

            TransaccionEntity entity = mapper.toEntity(domain);

            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(id);
            assertThat(entity.getCuentaOrigenId()).isEqualTo(cuentaOrigenId);
            assertThat(entity.getCuentaDestinoId()).isEqualTo(cuentaDestinoId);
            assertThat(entity.getTipoId()).isEqualTo((short) 1);
            assertThat(entity.getMonto()).isEqualByComparingTo("1500.50");
            assertThat(entity.getSaldoAnterior()).isEqualByComparingTo("5000.00");
            assertThat(entity.getSaldoPosterior()).isEqualByComparingTo("6500.50");
            assertThat(entity.getDescripcion()).isEqualTo("Transferencia a cuenta de ahorros");
            assertThat(entity.getReferencia()).isEqualTo("REF-001");
            assertThat(entity.getEstado()).isEqualTo(EstadoTransaccion.COMPLETADA);
            assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
            assertThat(entity.getCreatedBy()).isEqualTo("user@banco.com");
        }

        @Test
        @DisplayName("Debe retornar null cuando el domain es null")
        void debeRetornarNullCuandoDomainNull() {
            TransaccionEntity entity = mapper.toEntity(null);

            assertThat(entity).isNull();
        }

        @Test
        @DisplayName("Debe manejar campos null en el domain")
        void debeManejarCamposNullEnDomain() {
            Transaccion domain = Transaccion.builder()
                    .id(null)
                    .cuentaOrigenId(null)
                    .cuentaDestinoId(null)
                    .tipoId(null)
                    .monto(null)
                    .saldoAnterior(null)
                    .saldoPosterior(null)
                    .descripcion(null)
                    .referencia(null)
                    .estado(null)
                    .createdAt(null)
                    .createdBy(null)
                    .build();

            TransaccionEntity entity = mapper.toEntity(domain);

            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isNull();
            assertThat(entity.getCuentaOrigenId()).isNull();
            assertThat(entity.getCuentaDestinoId()).isNull();
            assertThat(entity.getTipoId()).isNull();
            assertThat(entity.getMonto()).isNull();
            assertThat(entity.getSaldoAnterior()).isNull();
            assertThat(entity.getSaldoPosterior()).isNull();
            assertThat(entity.getDescripcion()).isNull();
            assertThat(entity.getReferencia()).isNull();
            assertThat(entity.getEstado()).isNull();
            assertThat(entity.getCreatedAt()).isNull();
            assertThat(entity.getCreatedBy()).isEqualTo("SYSTEM");
        }

        @Test
        @DisplayName("Debe convertir transacción con solo cuenta origen")
        void debeConvertirTransaccionConSoloCuentaOrigen() {
            UUID cuentaOrigenId = UUID.randomUUID();

            Transaccion domain = Transaccion.builder()
                    .id(UUID.randomUUID())
                    .cuentaOrigenId(cuentaOrigenId)
                    .tipoId((short) 2)
                    .monto(new BigDecimal("100.00"))
                    .saldoAnterior(new BigDecimal("1000.00"))
                    .saldoPosterior(new BigDecimal("900.00"))
                    .estado(EstadoTransaccion.COMPLETADA)
                    .createdAt(OffsetDateTime.now())
                    .createdBy("system")
                    .build();

            TransaccionEntity entity = mapper.toEntity(domain);

            assertThat(entity.getCuentaOrigenId()).isEqualTo(cuentaOrigenId);
            assertThat(entity.getCuentaDestinoId()).isNull();
        }
    }

    @Nested
    @DisplayName("toDomain")
    class ToDomainTest {

        @Test
        @DisplayName("Debe convertir TransaccionEntity a Transaccion correctamente")
        void debeConvertirEntityADomain() {
            UUID id = UUID.randomUUID();
            UUID cuentaOrigenId = UUID.randomUUID();
            UUID cuentaDestinoId = UUID.randomUUID();
            OffsetDateTime createdAt = OffsetDateTime.now();

            TransaccionEntity entity = TransaccionEntity.builder()
                    .id(id)
                    .cuentaOrigenId(cuentaOrigenId)
                    .cuentaDestinoId(cuentaDestinoId)
                    .tipoId((short) 1)
                    .monto(new BigDecimal("1500.50"))
                    .saldoAnterior(new BigDecimal("5000.00"))
                    .saldoPosterior(new BigDecimal("6500.50"))
                    .descripcion("Transferencia a cuenta de ahorros")
                    .referencia("REF-001")
                    .estado(EstadoTransaccion.COMPLETADA)
                    .createdAt(createdAt)
                    .createdBy("user@banco.com")
                    .build();

            Transaccion domain = mapper.toDomain(entity);

            assertThat(domain).isNotNull();
            assertThat(domain.getId()).isEqualTo(id);
            assertThat(domain.getCuentaOrigenId()).isEqualTo(cuentaOrigenId);
            assertThat(domain.getCuentaDestinoId()).isEqualTo(cuentaDestinoId);
            assertThat(domain.getTipoId()).isEqualTo((short) 1);
            assertThat(domain.getMonto()).isEqualByComparingTo("1500.50");
            assertThat(domain.getSaldoAnterior()).isEqualByComparingTo("5000.00");
            assertThat(domain.getSaldoPosterior()).isEqualByComparingTo("6500.50");
            assertThat(domain.getDescripcion()).isEqualTo("Transferencia a cuenta de ahorros");
            assertThat(domain.getReferencia()).isEqualTo("REF-001");
            assertThat(domain.getEstado()).isEqualTo(EstadoTransaccion.COMPLETADA);
            assertThat(domain.getCreatedAt()).isEqualTo(createdAt);
            assertThat(domain.getCreatedBy()).isEqualTo("user@banco.com");
        }

        @Test
        @DisplayName("Debe retornar null cuando la entity es null")
        void debeRetornarNullCuandoEntityNull() {
            Transaccion domain = mapper.toDomain(null);

            assertThat(domain).isNull();
        }

        @Test
        @DisplayName("Debe manejar campos null en la entity")
        void debeManejarCamposNullEnEntity() {
            TransaccionEntity entity = TransaccionEntity.builder()
                    .id(null)
                    .cuentaOrigenId(null)
                    .cuentaDestinoId(null)
                    .tipoId(null)
                    .monto(null)
                    .saldoAnterior(null)
                    .saldoPosterior(null)
                    .descripcion(null)
                    .referencia(null)
                    .estado(null)
                    .createdAt(null)
                    .createdBy(null)
                    .build();

            Transaccion domain = mapper.toDomain(entity);

            assertThat(domain).isNotNull();
            assertThat(domain.getId()).isNull();
            assertThat(domain.getCuentaOrigenId()).isNull();
            assertThat(domain.getCuentaDestinoId()).isNull();
            assertThat(domain.getTipoId()).isNull();
            assertThat(domain.getMonto()).isNull();
            assertThat(domain.getSaldoAnterior()).isNull();
            assertThat(domain.getSaldoPosterior()).isNull();
            assertThat(domain.getDescripcion()).isNull();
            assertThat(domain.getReferencia()).isNull();
            assertThat(domain.getEstado()).isNull();
            assertThat(domain.getCreatedAt()).isNull();
            assertThat(domain.getCreatedBy()).isNull();
        }

        @Test
        @DisplayName("Debe convertir transacción con estado FALLIDA")
        void debeConvertirTransaccionConEstadoFallida() {
            TransaccionEntity entity = TransaccionEntity.builder()
                    .id(UUID.randomUUID())
                    .tipoId((short) 3)
                    .monto(new BigDecimal("500.00"))
                    .saldoAnterior(new BigDecimal("2000.00"))
                    .saldoPosterior(new BigDecimal("2000.00"))
                    .estado(EstadoTransaccion.FALLIDA)
                    .createdAt(OffsetDateTime.now())
                    .createdBy("system")
                    .build();

            Transaccion domain = mapper.toDomain(entity);

            assertThat(domain.getEstado()).isEqualTo(EstadoTransaccion.FALLIDA);
            assertThat(domain.getSaldoAnterior()).isEqualByComparingTo("2000.00");
            assertThat(domain.getSaldoPosterior()).isEqualByComparingTo("2000.00");
        }
    }

    @Nested
    @DisplayName("Bidirectional mapping")
    class BidirectionalMappingTest {

        @Test
        @DisplayName("Debe mantener datos al convertir domain → entity → domain")
        void debeMantenerDatosEnConversiónBidireccional() {
            UUID id = UUID.randomUUID();
            Transaccion original = Transaccion.builder()
                    .id(id)
                    .cuentaOrigenId(UUID.randomUUID())
                    .cuentaDestinoId(UUID.randomUUID())
                    .tipoId((short) 1)
                    .monto(new BigDecimal("999.99"))
                    .saldoAnterior(new BigDecimal("1000.00"))
                    .saldoPosterior(new BigDecimal("0.01"))
                    .descripcion("Pago de servicios")
                    .referencia("PAY-123")
                    .estado(EstadoTransaccion.COMPLETADA)
                    .createdAt(OffsetDateTime.now())
                    .createdBy("test@banco.com")
                    .build();

            TransaccionEntity entity = mapper.toEntity(original);
            Transaccion restored = mapper.toDomain(entity);

            assertThat(restored.getId()).isEqualTo(original.getId());
            assertThat(restored.getCuentaOrigenId()).isEqualTo(original.getCuentaOrigenId());
            assertThat(restored.getCuentaDestinoId()).isEqualTo(original.getCuentaDestinoId());
            assertThat(restored.getTipoId()).isEqualTo(original.getTipoId());
            assertThat(restored.getMonto()).isEqualByComparingTo(original.getMonto());
            assertThat(restored.getSaldoAnterior()).isEqualByComparingTo(original.getSaldoAnterior());
            assertThat(restored.getSaldoPosterior()).isEqualByComparingTo(original.getSaldoPosterior());
            assertThat(restored.getDescripcion()).isEqualTo(original.getDescripcion());
            assertThat(restored.getReferencia()).isEqualTo(original.getReferencia());
            assertThat(restored.getEstado()).isEqualTo(original.getEstado());
            assertThat(restored.getCreatedAt()).isEqualTo(original.getCreatedAt());
            assertThat(restored.getCreatedBy()).isEqualTo(original.getCreatedBy());
        }
    }
}
