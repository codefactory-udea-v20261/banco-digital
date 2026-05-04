package com.udea.bancodigital.transactions.infrastructure.adapter.out;

import com.udea.bancodigital.transactions.domain.model.Transaccion;
import com.udea.bancodigital.transactions.infrastructure.entity.TransaccionEntity;
import com.udea.bancodigital.transactions.infrastructure.mapper.TransaccionMapper;
import com.udea.bancodigital.transactions.infrastructure.repository.TransaccionJpaRepository;
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
class TransaccionRepositoryAdapterTest {

    @Mock
    private TransaccionJpaRepository repository;

    @Mock
    private TransaccionMapper mapper;

    @InjectMocks
    private TransaccionRepositoryAdapter adapter;

    @Test
    void save_ShouldSaveAndReturnDomain() {
        Transaccion domain = Transaccion.builder().id(UUID.randomUUID()).build();
        TransaccionEntity entity = new TransaccionEntity();
        
        when(mapper.toEntity(domain)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        Transaccion result = adapter.save(domain);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(domain.getId());
    }

    @Test
    void findByCuentaIdOrderByFechaDesc_ShouldReturnList() {
        UUID cuentaId = UUID.randomUUID();
        TransaccionEntity entity = new TransaccionEntity();
        Transaccion domain = Transaccion.builder().id(UUID.randomUUID()).build();

        when(repository.findByCuentaOrigenIdOrderByCreatedAtDesc(cuentaId)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Transaccion> result = adapter.findByCuentaIdOrderByFechaDesc(cuentaId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(domain.getId());
    }
}
