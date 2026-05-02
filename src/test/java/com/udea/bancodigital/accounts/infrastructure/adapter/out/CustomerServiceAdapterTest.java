package com.udea.bancodigital.accounts.infrastructure.adapter.out;

import com.udea.bancodigital.customers.domain.model.Cliente;
import com.udea.bancodigital.customers.domain.port.out.ClienteRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerServiceAdapter")
class CustomerServiceAdapterTest {

    @Mock
    private ClienteRepositoryPort clienteRepository;

    @InjectMocks
    private CustomerServiceAdapter adapter;

    @Nested
    @DisplayName("existeCliente")
    class ExisteClienteTest {

        @Test
        @DisplayName("Debe retornar true cuando el cliente existe")
        void debeRetornarTrueCuandoClienteExiste() {
            UUID clienteId = UUID.randomUUID();
            Cliente cliente = Cliente.builder()
                    .id(clienteId)
                    .build();

            when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));

            boolean result = adapter.existeCliente(clienteId);

            assertThat(result).isTrue();
            verify(clienteRepository).findById(clienteId);
        }

        @Test
        @DisplayName("Debe retornar false cuando el cliente no existe")
        void debeRetornarFalseCuandoClienteNoExiste() {
            UUID clienteId = UUID.randomUUID();

            when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

            boolean result = adapter.existeCliente(clienteId);

            assertThat(result).isFalse();
            verify(clienteRepository).findById(clienteId);
        }

        @Test
        @DisplayName("Debe retornar false para clienteId null")
        void debeRetornarFalseParaClienteIdNull() {
            when(clienteRepository.findById(null)).thenReturn(Optional.empty());

            boolean result = adapter.existeCliente(null);

            assertThat(result).isFalse();
            verify(clienteRepository).findById(null);
        }
    }

    @Nested
    @DisplayName("isClienteActivo")
    class IsClienteActivoTest {

        @Test
        @DisplayName("Debe retornar true cuando el cliente está activo")
        void debeRetornarTrueCuandoClienteActivo() {
            UUID clienteId = UUID.randomUUID();
            Cliente cliente = Cliente.builder()
                    .id(clienteId)
                    .activo(true)
                    .build();

            when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));

            boolean result = adapter.isClienteActivo(clienteId);

            assertThat(result).isTrue();
            verify(clienteRepository).findById(clienteId);
        }

        @Test
        @DisplayName("Debe retornar false cuando el cliente está inactivo")
        void debeRetornarFalseCuandoClienteInactivo() {
            UUID clienteId = UUID.randomUUID();
            Cliente cliente = Cliente.builder()
                    .id(clienteId)
                    .activo(false)
                    .build();

            when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));

            boolean result = adapter.isClienteActivo(clienteId);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Debe retornar false cuando el cliente no existe")
        void debeRetornarFalseCuandoClienteNoExiste() {
            UUID clienteId = UUID.randomUUID();

            when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

            boolean result = adapter.isClienteActivo(clienteId);

            assertThat(result).isFalse();
            verify(clienteRepository).findById(clienteId);
        }

        @Test
        @DisplayName("Debe retornar false para clienteId null")
        void debeRetornarFalseParaClienteIdNull() {
            when(clienteRepository.findById(null)).thenReturn(Optional.empty());

            boolean result = adapter.isClienteActivo(null);

            assertThat(result).isFalse();
            verify(clienteRepository).findById(null);
        }
    }
}
