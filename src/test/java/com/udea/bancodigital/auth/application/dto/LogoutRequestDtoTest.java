package com.udea.bancodigital.auth.application.dto;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class LogoutRequestDtoTest {

    @Nested
    class EqualsAndHashCode {
        @Test
        void shouldBeEqualWhenSameToken() {
            LogoutRequestDto d1 = LogoutRequestDto.builder().token("tok").build();
            LogoutRequestDto d2 = LogoutRequestDto.builder().token("tok").build();
            assertThat(d1).isEqualTo(d2).hasSameHashCodeAs(d2);
        }

        @Test
        void shouldNotBeEqualWhenTokenDiffers() {
            LogoutRequestDto d1 = LogoutRequestDto.builder().token("tok1").build();
            LogoutRequestDto d2 = LogoutRequestDto.builder().token("tok2").build();
            assertThat(d1).isNotEqualTo(d2);
        }

        @Test
        void shouldNotBeEqualWhenComparedWithNull() {
            LogoutRequestDto d = LogoutRequestDto.builder().token("tok").build();
            assertThat(d).isNotEqualTo(null);
        }

        @Test
        void shouldNotBeEqualWhenComparedWithDifferentType() {
            LogoutRequestDto d = LogoutRequestDto.builder().token("tok").build();
            assertThat(d).isNotEqualTo(123);
        }
    }

    @Nested
    class ToString {
        @Test
        void shouldContainToken() {
            LogoutRequestDto d = LogoutRequestDto.builder().token("my-token").build();
            assertThat(d.toString()).contains("my-token");
        }
    }

    @Nested
    class Getters {
        @Test
        void shouldReturnToken() {
            LogoutRequestDto d = new LogoutRequestDto("tok");
            assertThat(d.getToken()).isEqualTo("tok");
        }
    }

    @Nested
    class Setters {
        @Test
        void shouldSetToken() {
            LogoutRequestDto d = new LogoutRequestDto();
            d.setToken("new-tok");
            assertThat(d.getToken()).isEqualTo("new-tok");
        }
    }

    @Nested
    class Validation {
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void shouldFailValidationWhenTokenBlank(String token) {
            LogoutRequestDto d = new LogoutRequestDto(token);
            var validator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();
            var violations = validator.validate(d);
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("token"));
        }

        @Test
        void shouldPassValidationWhenTokenNotBlank() {
            LogoutRequestDto d = new LogoutRequestDto("valid-token");
            var validator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();
            var violations = validator.validate(d);
            assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("token"));
        }
    }

    @Nested
    class NoArgsConstructor {
        @Test
        void shouldCreateEmptyDto() {
            LogoutRequestDto d = new LogoutRequestDto();
            assertThat(d).isNotNull();
        }
    }

    @Nested
    class AllArgsConstructor {
        @Test
        void shouldCreateDtoWithToken() {
            LogoutRequestDto d = new LogoutRequestDto("tok");
            assertThat(d.getToken()).isEqualTo("tok");
        }
    }

    @Nested
    class Builder {
        @Test
        void shouldBuildDto() {
            LogoutRequestDto d = LogoutRequestDto.builder().token("tok").build();
            assertThat(d.getToken()).isEqualTo("tok");
        }
    }
}
