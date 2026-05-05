package com.udea.bancodigital.customers.application.mapper;

import com.udea.bancodigital.customers.domain.model.Cliente;
import com.udea.bancodigital.customers.domain.model.Email;
import com.udea.bancodigital.customers.domain.model.NumeroCedula;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("ClienteMapper Default Methods Tests")
class ClienteMapperDefaultMethodsTest {

    private final ClienteMapper mapper = new ClienteMapperImpl();

    @Test
    void shouldConvertStringToNumeroCedulaWhenValueNotNull() {
        String cedula = "12345678";
        NumeroCedula result = mapper.stringToNumeroCedula(cedula);
        
        assertNotNull(result);
        assertEquals(cedula, result.valor());
    }

    @Test
    void shouldReturnNullWhenStringToNumeroCedulaWithNull() {
        NumeroCedula result = mapper.stringToNumeroCedula(null);
        assertNull(result);
    }

    @Test
    void shouldConvertNumeroCedulaToStringWhenValueNotNull() {
        NumeroCedula cedula = NumeroCedula.of("87654321");
        String result = mapper.numeroCedulaToString(cedula);
        
        assertNotNull(result);
        assertEquals("87654321", result);
    }

    @Test
    void shouldReturnNullWhenNumeroCedulaToStringWithNull() {
        String result = mapper.numeroCedulaToString(null);
        assertNull(result);
    }

    @Test
    void shouldConvertStringToEmailWhenValueNotNull() {
        String emailStr = "test@example.com";
        Email result = mapper.stringToEmail(emailStr);
        
        assertNotNull(result);
        assertEquals(emailStr, result.valor());
    }

    @Test
    void shouldReturnNullWhenStringToEmailWithNull() {
        Email result = mapper.stringToEmail(null);
        assertNull(result);
    }

    @Test
    void shouldConvertEmailToStringWhenValueNotNull() {
        Email email = Email.of("user@mail.com");
        String result = mapper.emailToString(email);
        
        assertNotNull(result);
        assertEquals("user@mail.com", result);
    }

    @Test
    void shouldReturnNullWhenEmailToStringWithNull() {
        String result = mapper.emailToString(null);
        assertNull(result);
    }

    @Test
    void shouldHandleNullsInDefaultMethods() {
        assertNull(mapper.stringToNumeroCedula(null));
        assertNull(mapper.numeroCedulaToString(null));
        assertNull(mapper.stringToEmail(null));
        assertNull(mapper.emailToString(null));
    }
}
