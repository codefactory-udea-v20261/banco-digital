package com.udea.bancodigital.customers.application.mapper;

import com.udea.bancodigital.customers.domain.model.Email;
import com.udea.bancodigital.customers.domain.model.NumeroCedula;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClienteMapperInternalTest {

    private final ClienteMapper mapper = new ClienteMapper() {
        @Override public com.udea.bancodigital.customers.domain.model.Cliente toDomain(com.udea.bancodigital.customers.application.dto.CrearClienteRequestDto requestDto) { return null; }
        @Override public com.udea.bancodigital.customers.infrastructure.adapter.out.persistence.entity.ClienteEntity toEntity(com.udea.bancodigital.customers.domain.model.Cliente cliente) { return null; }
        @Override public com.udea.bancodigital.customers.domain.model.Cliente toDomain(com.udea.bancodigital.customers.infrastructure.adapter.out.persistence.entity.ClienteEntity entity) { return null; }
        @Override public com.udea.bancodigital.customers.application.dto.ClienteResponseDto toResponseDto(com.udea.bancodigital.customers.domain.model.Cliente cliente) { return null; }
        @Override public void updateFromDto(com.udea.bancodigital.customers.application.dto.ActualizarClienteRequestDto dto, com.udea.bancodigital.customers.domain.model.Cliente cliente) {}
    };

    @Test
    void testDefaultMethods() {
        assertNull(mapper.stringToNumeroCedula(null));
        assertNotNull(mapper.stringToNumeroCedula("1234567"));
        
        assertNull(mapper.numeroCedulaToString(null));
        assertEquals("1234567", mapper.numeroCedulaToString(NumeroCedula.of("1234567")));
        
        assertNull(mapper.stringToEmail(null));
        assertNotNull(mapper.stringToEmail("test@example.com"));
        
        assertNull(mapper.emailToString(null));
        assertEquals("test@example.com", mapper.emailToString(Email.of("test@example.com")));
    }
}
