package com.udea.bancodigital.customers.infrastructure.adapter.out.persistence;

import com.udea.bancodigital.customers.application.mapper.ClienteMapper;
import com.udea.bancodigital.customers.domain.model.Cliente;
import com.udea.bancodigital.customers.domain.model.Email;
import com.udea.bancodigital.customers.domain.model.NumeroCedula;
import com.udea.bancodigital.customers.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import com.udea.bancodigital.customers.infrastructure.adapter.out.persistence.repository.ClienteSpringRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteRepositoryAdapter")
class ClienteRepositoryAdapterTest {
    @Mock
    private ClienteSpringRepository repository;
    @Mock
    private ClienteMapper mapper;

    private ClienteRepositoryAdapter adapter;
    private Cliente clienteDominio;
    private ClienteEntity clienteEntity;

    @BeforeEach
    void setUp() {
        adapter = new ClienteRepositoryAdapter(repository, mapper);

        clienteDominio = Cliente.builder()
                .id(UUID.randomUUID())
                .numeroCedula(NumeroCedula.of("12345678"))
                .primerNombre("Juan")
                .primerApellido("Pérez")
                .email(Email.of("juan@banco.com"))
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .activo(true)
                .build();

        clienteEntity = ClienteEntity.builder()
                .id(clienteDominio.getId())
                .numeroCedula("12345678")
                .primerNombre("Juan")
                .primerApellido("Pérez")
                .email("juan@banco.com")
                .activo(true)
                .build();
    }

    @Nested
    @DisplayName("existsByEmail()")
    class ExistsByEmailTest {

        @Test
        @DisplayName("Debe retornar true cuando el email existe")
        void debeRetornarTrueCuandoEmailExiste() {
            when(repository.existsByEmail("juan@banco.com")).thenReturn(true);
            assertThat(adapter.existsByEmail("juan@banco.com")).isTrue();
            verify(repository).existsByEmail("juan@banco.com");
        }

        @Test
        @DisplayName("Debe retornar false cuando el email no existe")
        void debeRetornarFalseCuandoEmailNoExiste() {
            when(repository.existsByEmail("nuevo@banco.com")).thenReturn(false);
            assertThat(adapter.existsByEmail("nuevo@banco.com")).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByEmailAndIdNot()")
    class ExistsByEmailAndIdNotTest {

        @Test
        @DisplayName("Debe delegar correctamente al repositorio")
        void debeDelegarAlRepositorio() {
            UUID id = UUID.randomUUID();
            when(repository.existsByEmailAndIdNot("otro@banco.com", id)).thenReturn(true);
            assertThat(adapter.existsByEmailAndIdNot("otro@banco.com", id)).isTrue();
            verify(repository).existsByEmailAndIdNot("otro@banco.com", id);
        }

        @Test
        @DisplayName("Debe retornar false cuando no hay conflicto")
        void debeRetornarFalseSinConflicto() {
            UUID id = UUID.randomUUID();
            when(repository.existsByEmailAndIdNot("unico@banco.com", id)).thenReturn(false);
            assertThat(adapter.existsByEmailAndIdNot("unico@banco.com", id)).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByCedula()")
    class ExistsByCedulaTest {

        @Test
        @DisplayName("Debe retornar true cuando la cédula existe")
        void debeRetornarTrueCuandoCedulaExiste() {
            when(repository.existsByNumeroCedula("12345678")).thenReturn(true);
            assertThat(adapter.existsByCedula("12345678")).isTrue();
            verify(repository).existsByNumeroCedula("12345678");
        }

        @Test
        @DisplayName("Debe retornar false cuando la cédula no existe")
        void debeRetornarFalseCuandoCedulaNoExiste() {
            when(repository.existsByNumeroCedula("99999999")).thenReturn(false);
            assertThat(adapter.existsByCedula("99999999")).isFalse();
        }
    }

    @Nested
    @DisplayName("save()")
    class SaveTest {

        @Test
        @DisplayName("Debe mapear a entity, guardar y mapear de vuelta al dominio")
        void debeGuardarYRetornarDominio() {
            when(mapper.toEntity(clienteDominio)).thenReturn(clienteEntity);
            when(repository.save(clienteEntity)).thenReturn(clienteEntity);
            when(mapper.toDomain(clienteEntity)).thenReturn(clienteDominio);

            Cliente resultado = adapter.save(clienteDominio);

            assertThat(resultado).isEqualTo(clienteDominio);
            verify(mapper).toEntity(clienteDominio);
            verify(repository).save(clienteEntity);
            verify(mapper).toDomain(clienteEntity);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTest {

        @Test
        @DisplayName("Debe retornar Optional con cliente cuando existe")
        void debeRetornarOptionalPresenteCuandoExiste() {
            UUID id = clienteDominio.getId();
            when(repository.findById(id)).thenReturn(Optional.of(clienteEntity));
            when(mapper.toDomain(clienteEntity)).thenReturn(clienteDominio);

            Optional<Cliente> resultado = adapter.findById(id);

            assertThat(resultado).isPresent().contains(clienteDominio);
        }

        @Test
        @DisplayName("Debe retornar Optional vacío cuando no existe")
        void debeRetornarOptionalVacioCuandoNoExiste() {
            UUID id = UUID.randomUUID();
            when(repository.findById(id)).thenReturn(Optional.empty());

            Optional<Cliente> resultado = adapter.findById(id);

            assertThat(resultado).isEmpty();
            verify(mapper, never()).toDomain(any(ClienteEntity.class));
        }
    }

}
