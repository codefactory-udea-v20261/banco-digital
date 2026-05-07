package com.udea.bancodigital.customers.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValueObjectDomainTest {

    @Test
    void testEmailValidation() {
        assertThrows(NullPointerException.class, () -> new Email(null));
        assertThrows(IllegalArgumentException.class, () -> new Email(""));
        assertThrows(IllegalArgumentException.class, () -> new Email("invalid-email"));
        assertThrows(IllegalArgumentException.class, () -> new Email("a".repeat(250) + "@example.com"));
        
        Email email = Email.of("test@example.com");
        assertEquals("test@example.com", email.valor());
        assertEquals("test@example.com", email.toString());
    }

    @Test
    void testNumeroCedulaValidation() {
        assertThrows(NullPointerException.class, () -> new NumeroCedula(null));
        assertThrows(IllegalArgumentException.class, () -> new NumeroCedula(""));
        assertThrows(IllegalArgumentException.class, () -> new NumeroCedula("123"));
        assertThrows(IllegalArgumentException.class, () -> new NumeroCedula("abc12345"));
        
        NumeroCedula cedula = NumeroCedula.of("12345678");
        assertEquals("12345678", cedula.valor());
        assertEquals("12345678", cedula.toString());
    }
}
