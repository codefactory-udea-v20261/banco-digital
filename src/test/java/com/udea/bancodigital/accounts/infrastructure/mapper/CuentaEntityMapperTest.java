package com.udea.bancodigital.accounts.infrastructure.mapper;

import com.udea.bancodigital.accounts.domain.model.Cuenta;
import com.udea.bancodigital.accounts.domain.model.EstadoCuenta;
import com.udea.bancodigital.accounts.domain.model.TipoCuenta;
import com.udea.bancodigital.accounts.infrastructure.entity.CuentaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CuentaEntityMapper")
class CuentaEntityMapperTest {

    private final CuentaEntityMapper mapper = new CuentaEntityMapper();

    @Nested
    @DisplayName("toDomain")
    class ToDomainTest {

        @Test
        @DisplayName("Debe convertir CuentaEntity a Cuenta correctamente")
        void debeConvertirEntityADomain() {
            UUID id = UUID.randomUUID();
            UUID clienteId = UUID.randomUUID();
            LocalDate fechaApertura = LocalDate.of(2024, 1, 15);

            CuentaEntity entity = CuentaEntity.builder()
                    .id(id)
                    .numeroCuenta("1234567890")
                    .clienteId(clienteId)
                    .tipoCuentaId((short) 1)
                    .saldo(new BigDecimal("5000.50"))
                    .estado("ACTIVA")
                    .fechaApertura(fechaApertura)
                    .build();

            Cuenta result = mapper.toDomain(entity);

            assertThat(result).isNotNull()
                    .extracting(Cuenta::getId, Cuenta::getNumeroCuenta, Cuenta::getClienteId)
                    .containsExactly(id, "1234567890", clienteId);

            assertThat(result.getTipoCuenta()).isEqualTo(TipoCuenta.AHORRO);
            assertThat(result.getSaldo()).isEqualTo(new BigDecimal("5000.50"));
            assertThat(result.getEstado()).isEqualTo(EstadoCuenta.ACTIVA);
            assertThat(result.getFechaApertura()).isEqualTo(fechaApertura);
        }

        @Test
        @DisplayName("Debe convertir tipo de cuenta CORRIENTE correctamente")
        void debeConvertirTipoCuentaCorriente() {
            CuentaEntity entity = CuentaEntity.builder()
                    .id(UUID.randomUUID())
                    .numeroCuenta("9876543210")
                    .clienteId(UUID.randomUUID())
                    .tipoCuentaId((short) 2)
                    .saldo(new BigDecimal("10000.00"))
                    .estado("ACTIVA")
                    .fechaApertura(LocalDate.now())
                    .build();

            Cuenta result = mapper.toDomain(entity);

            assertThat(result.getTipoCuenta()).isEqualTo(TipoCuenta.CORRIENTE);
        }

        @Test
        @DisplayName("Debe convertir estado INACTIVA correctamente")
        void debeConvertirEstadoInactiva() {
            CuentaEntity entity = CuentaEntity.builder()
                    .id(UUID.randomUUID())
                    .numeroCuenta("1111111111")
                    .clienteId(UUID.randomUUID())
                    .tipoCuentaId((short) 1)
                    .saldo(new BigDecimal("0.00"))
                    .estado("INACTIVA")
                    .fechaApertura(LocalDate.now())
                    .build();

            Cuenta result = mapper.toDomain(entity);

            assertThat(result.getEstado()).isEqualTo(EstadoCuenta.INACTIVA);
        }

        @Test
        @DisplayName("Debe convertir estado BLOQUEADA correctamente")
        void debeConvertirEstadoBloqueada() {
            CuentaEntity entity = CuentaEntity.builder()
                    .id(UUID.randomUUID())
                    .numeroCuenta("2222222222")
                    .clienteId(UUID.randomUUID())
                    .tipoCuentaId((short) 2)
                    .saldo(new BigDecimal("5000.00"))
                    .estado("BLOQUEADA")
                    .fechaApertura(LocalDate.now())
                    .build();

            Cuenta result = mapper.toDomain(entity);

            assertThat(result.getEstado()).isEqualTo(EstadoCuenta.BLOQUEADA);
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntityTest {

        @Test
        @DisplayName("Debe convertir Cuenta a CuentaEntity correctamente")
        void debeConvertirDomainAEntity() {
            UUID id = UUID.randomUUID();
            UUID clienteId = UUID.randomUUID();
            LocalDate fechaApertura = LocalDate.of(2024, 2, 20);

            Cuenta domain = Cuenta.builder()
                    .id(id)
                    .numeroCuenta("5555555555")
                    .clienteId(clienteId)
                    .tipoCuenta(TipoCuenta.AHORRO)
                    .saldo(new BigDecimal("15000.75"))
                    .estado(EstadoCuenta.ACTIVA)
                    .fechaApertura(fechaApertura)
                    .build();

            CuentaEntity result = mapper.toEntity(domain);

            assertThat(result).isNotNull()
                    .extracting(CuentaEntity::getId, CuentaEntity::getNumeroCuenta, CuentaEntity::getClienteId)
                    .containsExactly(id, "5555555555", clienteId);

            assertThat(result.getTipoCuentaId()).isEqualTo((short) 1);
            assertThat(result.getSaldo()).isEqualTo(new BigDecimal("15000.75"));
            assertThat(result.getEstado()).isEqualTo("ACTIVA");
            assertThat(result.getFechaApertura()).isEqualTo(fechaApertura);
        }

        @Test
        @DisplayName("Debe convertir Cuenta CORRIENTE a entity con id 2")
        void debeConvertirCuentaCorrienteConId2() {
            Cuenta domain = Cuenta.builder()
                    .id(UUID.randomUUID())
                    .numeroCuenta("6666666666")
                    .clienteId(UUID.randomUUID())
                    .tipoCuenta(TipoCuenta.CORRIENTE)
                    .saldo(new BigDecimal("20000.00"))
                    .estado(EstadoCuenta.ACTIVA)
                    .fechaApertura(LocalDate.now())
                    .build();

            CuentaEntity result = mapper.toEntity(domain);

            assertThat(result.getTipoCuentaId()).isEqualTo((short) 2);
        }

        @Test
        @DisplayName("Debe convertir estado INACTIVA a string")
        void debeConvertirEstadoInactivaAString() {
            Cuenta domain = Cuenta.builder()
                    .id(UUID.randomUUID())
                    .numeroCuenta("7777777777")
                    .clienteId(UUID.randomUUID())
                    .tipoCuenta(TipoCuenta.AHORRO)
                    .saldo(BigDecimal.ZERO)
                    .estado(EstadoCuenta.INACTIVA)
                    .fechaApertura(LocalDate.now())
                    .build();

            CuentaEntity result = mapper.toEntity(domain);

            assertThat(result.getEstado()).isEqualTo("INACTIVA");
        }

        @Test
        @DisplayName("Debe convertir estado BLOQUEADA a string")
        void debeConvertirEstadoBloqueadaAString() {
            Cuenta domain = Cuenta.builder()
                    .id(UUID.randomUUID())
                    .numeroCuenta("8888888888")
                    .clienteId(UUID.randomUUID())
                    .tipoCuenta(TipoCuenta.CORRIENTE)
                    .saldo(new BigDecimal("5000.00"))
                    .estado(EstadoCuenta.BLOQUEADA)
                    .fechaApertura(LocalDate.now())
                    .build();

            CuentaEntity result = mapper.toEntity(domain);

            assertThat(result.getEstado()).isEqualTo("BLOQUEADA");
        }
    }

    @Nested
    @DisplayName("Bidirectional conversion")
    class BidirectionalTest {

        @Test
        @DisplayName("Debe ser consistente al convertir entity -> domain -> entity")
        void debeSerConsistenteEntityDomainEntity() {
            UUID id = UUID.randomUUID();
            UUID clienteId = UUID.randomUUID();
            LocalDate fechaApertura = LocalDate.of(2024, 3, 10);

            CuentaEntity original = CuentaEntity.builder()
                    .id(id)
                    .numeroCuenta("9999999999")
                    .clienteId(clienteId)
                    .tipoCuentaId((short) 1)
                    .saldo(new BigDecimal("25000.99"))
                    .estado("ACTIVA")
                    .fechaApertura(fechaApertura)
                    .build();

            Cuenta domain = mapper.toDomain(original);
            CuentaEntity result = mapper.toEntity(domain);

            assertThat(result)
                    .extracting(CuentaEntity::getId, CuentaEntity::getNumeroCuenta, CuentaEntity::getClienteId,
                            CuentaEntity::getTipoCuentaId, CuentaEntity::getSaldo, CuentaEntity::getEstado,
                            CuentaEntity::getFechaApertura)
                    .containsExactly(id, "9999999999", clienteId, (short) 1,
                            new BigDecimal("25000.99"), "ACTIVA", fechaApertura);
        }

        @Test
        @DisplayName("Debe ser consistente al convertir domain -> entity -> domain")
        void debeSerConsistenteDomainEntityDomain() {
            UUID id = UUID.randomUUID();
            UUID clienteId = UUID.randomUUID();
            LocalDate fechaApertura = LocalDate.of(2024, 4, 5);

            Cuenta original = Cuenta.builder()
                    .id(id)
                    .numeroCuenta("3333333333")
                    .clienteId(clienteId)
                    .tipoCuenta(TipoCuenta.CORRIENTE)
                    .saldo(new BigDecimal("30000.00"))
                    .estado(EstadoCuenta.ACTIVA)
                    .fechaApertura(fechaApertura)
                    .build();

            CuentaEntity entity = mapper.toEntity(original);
            Cuenta result = mapper.toDomain(entity);

            assertThat(result)
                    .extracting(Cuenta::getId, Cuenta::getNumeroCuenta, Cuenta::getClienteId,
                            Cuenta::getTipoCuenta, Cuenta::getSaldo, Cuenta::getEstado,
                            Cuenta::getFechaApertura)
                    .containsExactly(id, "3333333333", clienteId, TipoCuenta.CORRIENTE,
                            new BigDecimal("30000.00"), EstadoCuenta.ACTIVA, fechaApertura);
        }
    }
}
