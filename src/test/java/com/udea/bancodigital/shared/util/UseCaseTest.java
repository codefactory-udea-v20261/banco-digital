package com.udea.bancodigital.shared.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UseCaseTest {

    @Test
    void testUseCaseInterface() {
        UseCase<String, Integer> useCase = input -> Integer.parseInt(input);
        assertEquals(10, useCase.ejecutar("10"));
    }

    @Test
    void testUseCaseNoInputInterface() {
        UseCaseNoInput<String> useCase = () -> "Hello";
        assertEquals("Hello", useCase.ejecutar());
    }
}
