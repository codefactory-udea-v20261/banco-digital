package com.udea.bancodigital.auth.domain.exception;

import com.udea.bancodigital.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TokenRevocadoExceptionTest {

    @Test
    void shouldCreateExceptionWithCorrectProperties() {
        TokenRevocadoException exception = new TokenRevocadoException();

        assertNotNull(exception);
        assertInstanceOf(BusinessException.class, exception);
    }

    @Test
    void shouldHaveCorrectCode() {
        TokenRevocadoException exception = new TokenRevocadoException();
        assertEquals("TOKEN_REVOCADO", exception.getErrorCode());
    }

    @Test
    void shouldHaveCorrectMessage() {
        TokenRevocadoException exception = new TokenRevocadoException();
        assertEquals(
            "El token de acceso ha sido revocado. Por favor inicie sesión nuevamente",
            exception.getMessage()
        );
    }

    @Test
    void shouldHaveCorrectHttpStatus() {
        TokenRevocadoException exception = new TokenRevocadoException();
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());
    }

    @Test
    void shouldBeInstanceOfException() {
        TokenRevocadoException exception = new TokenRevocadoException();
        assertInstanceOf(Exception.class, exception);
    }
}
