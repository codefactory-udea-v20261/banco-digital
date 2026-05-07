package com.udea.bancodigital.customers.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    void testClienteDomainModel() {
        UUID id = UUID.randomUUID();
        NumeroCedula cedula = new NumeroCedula("12345678");
        Email email = new Email("test@example.com");
        LocalDate birth = LocalDate.of(1990, 1, 1);
        
        Cliente cliente = Cliente.builder()
                .id(id)
                .numeroCedula(cedula)
                .primerNombre("Juan")
                .primerApellido("Perez")
                .email(email)
                .fechaNacimiento(birth)
                .activo(true)
                .build();
        
        assertEquals(id, cliente.getId());
        assertEquals(cedula, cliente.getNumeroCedula());
        assertEquals("Juan", cliente.getPrimerNombre());
        assertEquals(email, cliente.getEmail());
        assertTrue(cliente.isActivo());
        
        // Test @With (Lombok)
        Cliente inactive = cliente.withActivo(false);
        assertFalse(inactive.isActivo());
        assertEquals(cliente.getId(), inactive.getId());
    }
}
