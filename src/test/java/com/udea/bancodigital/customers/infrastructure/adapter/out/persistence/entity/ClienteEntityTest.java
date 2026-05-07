package com.udea.bancodigital.customers.infrastructure.adapter.out.persistence.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ClienteEntityTest {

    // Helper class to expose protected methods for testing if needed,
    // or just rely on the fact that prePersist is called by JPA.
    // Here we can use reflection or a subclass in the same package.
    static class TestClienteEntity extends ClienteEntity {
        public void triggerPrePersist() {
            super.prePersist();
        }
    }

    @Test
    void testClienteEntity_GettersSettersAndAuditing() {
        UUID id = UUID.randomUUID();
        LocalDate birth = LocalDate.of(1990, 1, 1);
        
        TestClienteEntity cliente = new TestClienteEntity();
        cliente.setId(id);
        cliente.setNumeroCedula("12345");
        cliente.setPrimerNombre("Juan");
        cliente.setPrimerApellido("Perez");
        cliente.setEmail("juan@example.com");
        cliente.setFechaNacimiento(birth);
        cliente.setActivo(true);

        // Testing getters
        assertEquals(id, cliente.getId());
        assertEquals("12345", cliente.getNumeroCedula());
        assertEquals("Juan", cliente.getPrimerNombre());
        assertEquals("Perez", cliente.getPrimerApellido());
        assertEquals("juan@example.com", cliente.getEmail());
        assertEquals(birth, cliente.getFechaNacimiento());
        assertTrue(cliente.isActivo());

        // Testing setters from AuditableEntity
        cliente.setCreatedBy("ADMIN");
        assertEquals("ADMIN", cliente.getCreatedBy());
        
        // Testing prePersist
        cliente.triggerPrePersist();
        assertNotNull(cliente.getCreatedAt());
        assertEquals("ADMIN", cliente.getCreatedBy());
    }
    
    @Test
    void testNoArgsConstructor() {
        ClienteEntity cliente = new ClienteEntity();
        assertNotNull(cliente);
        cliente.setPrimerNombre("Test");
        assertEquals("Test", cliente.getPrimerNombre());
    }
}
