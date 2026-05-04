package com.udea.bancodigital.accounts.infrastructure.adapter.out;

import com.udea.bancodigital.accounts.domain.model.Cuenta;
import com.udea.bancodigital.accounts.domain.model.EstadoCuenta;
import com.udea.bancodigital.accounts.domain.model.TipoCuenta;
import com.udea.bancodigital.accounts.infrastructure.entity.CuentaEntity;
import com.udea.bancodigital.accounts.infrastructure.mapper.CuentaEntityMapper;
import com.udea.bancodigital.accounts.infrastructure.repository.CuentaJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CuentaRepositoryAdapter")
class CuentaRepositoryAdapterTest {

    @Mock
    private CuentaJpaRepository jpaRepository;

    @Mock
    private CuentaEntityMapper mapper;

    @InjectMocks
    private CuentaRepositoryAdapter adapter;

    @Nested
    @DisplayName("save")
    class SaveTest {

        @Test
        @DisplayName("Debe guardar cuenta y retornar el modelo de dominio")
        void debeGuardarCuentaYRetornarModelo() {
            UUID id = UUID.randomUUID();
            UUID clienteId = UUID.randomUUID();
            Cuenta cuenta = Cuenta.builder()
                    .id(id)
                    .numeroCuenta("1234567890")
                    .clienteId(clienteId)
                    .tipoCuenta(TipoCuenta.AHORRO)
                    .saldo(BigDecimal.valueOf(1000))
                    .estado(EstadoCuenta.ACTIVA)
                    .fechaApertura(LocalDate.now())
                    .build();

            CuentaEntity entity = CuentaEntity.builder()
                    .id(id)
                    .numeroCuenta("1234567890")
                    .clienteId(clienteId)
                    .tipoCuentaId(TipoCuenta.AHORRO.getId())
                    .saldo(BigDecimal.valueOf(1000))
                    .estado(EstadoCuenta.ACTIVA.name())
                    .fechaApertura(LocalDate.now())
                    .build();

            CuentaEntity savedEntity = CuentaEntity.builder()
                    .id(id)
                    .numeroCuenta("1234567890")
                    .clienteId(clienteId)
                    .tipoCuentaId(TipoCuenta.AHORRO.getId())
                    .saldo(BigDecimal.valueOf(1000))
                    .estado(EstadoCuenta.ACTIVA.name())
                    .fechaApertura(LocalDate.now())
                    .build();

            when(mapper.toEntity(cuenta)).thenReturn(entity);
            when(jpaRepository.save(entity)).thenReturn(savedEntity);
            when(mapper.toDomain(savedEntity)).thenReturn(cuenta);

            Cuenta result = adapter.save(cuenta);

            assertThat(result).isEqualTo(cuenta);
            verify(mapper).toEntity(cuenta);
            verify(jpaRepository).save(entity);
            verify(mapper).toDomain(savedEntity);
        }

        @Test
        @DisplayName("Debe pasar la entidad correcta al repositorio JPA")
        void debePasarEntidadCorrectaAlRepositorio() {
            UUID id = UUID.randomUUID();
            Cuenta cuenta = Cuenta.builder()
                    .id(id)
                    .numeroCuenta("9876543210")
                    .clienteId(UUID.randomUUID())
                    .tipoCuenta(TipoCuenta.CORRIENTE)
                    .saldo(BigDecimal.ZERO)
                    .estado(EstadoCuenta.ACTIVA)
                    .fechaApertura(LocalDate.now())
                    .build();

            CuentaEntity entity = CuentaEntity.builder()
                    .id(id)
                    .numeroCuenta("9876543210")
                    .build();

            when(mapper.toEntity(cuenta)).thenReturn(entity);
            when(jpaRepository.save(any(CuentaEntity.class))).thenReturn(entity);
            when(mapper.toDomain(any(CuentaEntity.class))).thenReturn(cuenta);

            adapter.save(cuenta);

            ArgumentCaptor<CuentaEntity> captor = ArgumentCaptor.forClass(CuentaEntity.class);
            verify(jpaRepository).save(captor.capture());
            assertThat(captor.getValue().getNumeroCuenta()).isEqualTo("9876543210");
        }

        @Test
        @DisplayName("Debe manejar cuenta con saldo cero")
        void debeManejarCuentaConSaldoCero() {
            Cuenta cuenta = Cuenta.builder()
                    .id(UUID.randomUUID())
                    .numeroCuenta("1111111111")
                    .clienteId(UUID.randomUUID())
                    .tipoCuenta(TipoCuenta.AHORRO)
                    .saldo(BigDecimal.ZERO)
                    .estado(EstadoCuenta.ACTIVA)
                    .fechaApertura(LocalDate.now())
                    .build();

            CuentaEntity entity = CuentaEntity.builder()
                    .id(cuenta.getId())
                    .numeroCuenta("1111111111")
                    .saldo(BigDecimal.ZERO)
                    .build();

            when(mapper.toEntity(any(Cuenta.class))).thenReturn(entity);
            when(jpaRepository.save(any(CuentaEntity.class))).thenReturn(entity);
            when(mapper.toDomain(any(CuentaEntity.class))).thenReturn(cuenta);

            Cuenta result = adapter.save(cuenta);

            assertThat(result.getSaldo()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Debe manejar cuenta con estado INACTIVA")
        void debeManejarCuentaConEstadoInactiva() {
            Cuenta cuenta = Cuenta.builder()
                    .id(UUID.randomUUID())
                    .numeroCuenta("2222222222")
                    .clienteId(UUID.randomUUID())
                    .tipoCuenta(TipoCuenta.CORRIENTE)
                    .saldo(BigDecimal.valueOf(500))
                    .estado(EstadoCuenta.INACTIVA)
                    .fechaApertura(LocalDate.now())
                    .build();

            CuentaEntity entity = CuentaEntity.builder()
                    .id(cuenta.getId())
                    .estado(EstadoCuenta.INACTIVA.name())
                    .build();

            when(mapper.toEntity(any(Cuenta.class))).thenReturn(entity);
            when(jpaRepository.save(any(CuentaEntity.class))).thenReturn(entity);
            when(mapper.toDomain(any(CuentaEntity.class))).thenReturn(cuenta);

            Cuenta result = adapter.save(cuenta);

            assertThat(result.getEstado()).isEqualTo(EstadoCuenta.INACTIVA);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("Debe retornar cuenta cuando existe")
        void debeRetornarCuentaCuandoExiste() {
            UUID id = UUID.randomUUID();
            CuentaEntity entity = CuentaEntity.builder()
                    .id(id)
                    .numeroCuenta("1234567890")
                    .build();
            Cuenta cuenta = Cuenta.builder()
                    .id(id)
                    .numeroCuenta("1234567890")
                    .build();

            when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
            when(mapper.toDomain(entity)).thenReturn(cuenta);

            Optional<Cuenta> result = adapter.findById(id);

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(cuenta);
            verify(jpaRepository).findById(id);
            verify(mapper).toDomain(entity);
        }

        @Test
        @DisplayName("Debe retornar Optional vacío cuando no existe")
        void debeRetornarVacioCuandoNoExiste() {
            UUID id = UUID.randomUUID();

            when(jpaRepository.findById(id)).thenReturn(Optional.empty());

            Optional<Cuenta> result = adapter.findById(id);

            assertThat(result).isEmpty();
            verify(jpaRepository).findById(id);
        }

        @Test
        @DisplayName("Debe manejar id null")
        void debeManejarIdNull() {
            when(jpaRepository.findById(null)).thenReturn(Optional.empty());

            Optional<Cuenta> result = adapter.findById(null);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByNumeroCuenta")
    class FindByNumeroCuentaTest {

        @Test
        @DisplayName("Debe retornar cuenta cuando el número existe")
        void debeRetornarCuentaCuandoNumeroExiste() {
            String numeroCuenta = "1234567890";
            CuentaEntity entity = CuentaEntity.builder()
                    .id(UUID.randomUUID())
                    .numeroCuenta(numeroCuenta)
                    .build();
            Cuenta cuenta = Cuenta.builder()
                    .id(entity.getId())
                    .numeroCuenta(numeroCuenta)
                    .build();

            when(jpaRepository.findByNumeroCuenta(numeroCuenta)).thenReturn(Optional.of(entity));
            when(mapper.toDomain(entity)).thenReturn(cuenta);

            Optional<Cuenta> result = adapter.findByNumeroCuenta(numeroCuenta);

            assertThat(result).isPresent();
            assertThat(result.get().getNumeroCuenta()).isEqualTo(numeroCuenta);
            verify(jpaRepository).findByNumeroCuenta(numeroCuenta);
        }

        @Test
        @DisplayName("Debe retornar Optional vacío cuando el número no existe")
        void debeRetornarVacioCuandoNumeroNoExiste() {
            String numeroCuenta = "9999999999";

            when(jpaRepository.findByNumeroCuenta(numeroCuenta)).thenReturn(Optional.empty());

            Optional<Cuenta> result = adapter.findByNumeroCuenta(numeroCuenta);

            assertThat(result).isEmpty();
            verify(jpaRepository).findByNumeroCuenta(numeroCuenta);
        }

        @Test
        @DisplayName("Debe manejar número de cuenta null")
        void debeManejarNumeroCuentaNull() {
            when(jpaRepository.findByNumeroCuenta(null)).thenReturn(Optional.empty());

            Optional<Cuenta> result = adapter.findByNumeroCuenta(null);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Debe manejar número de cuenta vacío")
        void debeManejarNumeroCuentaVacio() {
            when(jpaRepository.findByNumeroCuenta("")).thenReturn(Optional.empty());

            Optional<Cuenta> result = adapter.findByNumeroCuenta("");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByNumeroCuenta")
    class ExistsByNumeroCuentaTest {

        @Test
        @DisplayName("Debe retornar true cuando la cuenta existe")
        void debeRetornarTrueCuandoExiste() {
            String numeroCuenta = "1234567890";

            when(jpaRepository.existsByNumeroCuenta(numeroCuenta)).thenReturn(true);

            boolean exists = adapter.existsByNumeroCuenta(numeroCuenta);

            assertThat(exists).isTrue();
            verify(jpaRepository).existsByNumeroCuenta(numeroCuenta);
        }

        @Test
        @DisplayName("Debe retornar false cuando la cuenta no existe")
        void debeRetornarFalseCuandoNoExiste() {
            String numeroCuenta = "9999999999";

            when(jpaRepository.existsByNumeroCuenta(numeroCuenta)).thenReturn(false);

            boolean exists = adapter.existsByNumeroCuenta(numeroCuenta);

            assertThat(exists).isFalse();
            verify(jpaRepository).existsByNumeroCuenta(numeroCuenta);
        }

        @Test
        @DisplayName("Debe retornar false para número de cuenta vacío")
        void debeRetornarFalseParaNumeroVacio() {
            when(jpaRepository.existsByNumeroCuenta("")).thenReturn(false);

            boolean exists = adapter.existsByNumeroCuenta("");

            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("Debe retornar false para número de cuenta null")
        void debeRetornarFalseParaNumeroNull() {
            when(jpaRepository.existsByNumeroCuenta(null)).thenReturn(false);

            boolean exists = adapter.existsByNumeroCuenta(null);

            assertThat(exists).isFalse();
        }
    }
}
