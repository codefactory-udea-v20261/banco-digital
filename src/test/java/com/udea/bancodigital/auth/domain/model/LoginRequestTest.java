package com.udea.bancodigital.auth.domain.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    @Nested
    class EqualsAndHashCode {
        @Test
        void shouldBeEqualWhenAllFieldsSame() {
            LoginRequest r1 = LoginRequest.builder()
                .correo("a@b.com").clave("123").mfaCode("000000").build();
            LoginRequest r2 = LoginRequest.builder()
                .correo("a@b.com").clave("123").mfaCode("000000").build();
            assertThat(r1).isEqualTo(r2).hasSameHashCodeAs(r2);
        }

        @Test
        void shouldNotBeEqualWhenCorreoDiffers() {
            LoginRequest r1 = LoginRequest.builder()
                .correo("a@b.com").clave("123").mfaCode("000000").build();
            LoginRequest r2 = LoginRequest.builder()
                .correo("x@y.com").clave("123").mfaCode("000000").build();
            assertThat(r1).isNotEqualTo(r2);
        }

        @Test
        void shouldNotBeEqualWhenClaveDiffers() {
            LoginRequest r1 = LoginRequest.builder()
                .correo("a@b.com").clave("123").mfaCode("000000").build();
            LoginRequest r2 = LoginRequest.builder()
                .correo("a@b.com").clave("456").mfaCode("000000").build();
            assertThat(r1).isNotEqualTo(r2);
        }

        @Test
        void shouldNotBeEqualWhenMfaCodeDiffers() {
            LoginRequest r1 = LoginRequest.builder()
                .correo("a@b.com").clave("123").mfaCode("000000").build();
            LoginRequest r2 = LoginRequest.builder()
                .correo("a@b.com").clave("123").mfaCode("111111").build();
            assertThat(r1).isNotEqualTo(r2);
        }

        @Test
        void shouldNotBeEqualWhenComparedWithNull() {
            LoginRequest r = LoginRequest.builder()
                .correo("a@b.com").clave("123").mfaCode("000000").build();
            assertThat(r).isNotEqualTo(null);
        }

        @Test
        void shouldNotBeEqualWhenComparedWithDifferentType() {
            LoginRequest r = LoginRequest.builder()
                .correo("a@b.com").clave("123").mfaCode("000000").build();
            assertThat(r).isNotEqualTo("string");
        }
    }

    @Nested
    class ToString {
        @Test
        void shouldContainFieldValues() {
            LoginRequest r = LoginRequest.builder()
                .correo("a@b.com").clave("123").mfaCode("000000").build();
            String s = r.toString();
            assertThat(s).contains("a@b.com", "123", "000000");
        }
    }

    @Nested
    class Getters {
        @Test
        void shouldReturnCorrectValues() {
            LoginRequest r = LoginRequest.builder()
                .correo("a@b.com").clave("123").mfaCode("000000").build();
            assertThat(r.getCorreo()).isEqualTo("a@b.com");
            assertThat(r.getClave()).isEqualTo("123");
            assertThat(r.getMfaCode()).isEqualTo("000000");
        }
    }

    @Nested
    class Builder {
        @Test
        void shouldBuildWithAllFields() {
            LoginRequest r = LoginRequest.builder()
                .correo("a@b.com").clave("123").mfaCode("000000").build();
            assertThat(r).isNotNull();
            assertThat(r.getCorreo()).isEqualTo("a@b.com");
        }
    }
}
