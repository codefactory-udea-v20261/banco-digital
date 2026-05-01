package com.udea.bancodigital.auth.application.dto;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginResponseDtoTest {

    @Nested
    class EqualsAndHashCode {
        @Test
        void shouldBeEqualWhenSameToken() {
            LoginResponseDto d1 = LoginResponseDto.builder().token("tok").build();
            LoginResponseDto d2 = LoginResponseDto.builder().token("tok").build();
            assertThat(d1).isEqualTo(d2).hasSameHashCodeAs(d2);
        }

        @Test
        void shouldNotBeEqualWhenTokenDiffers() {
            LoginResponseDto d1 = LoginResponseDto.builder().token("tok1").build();
            LoginResponseDto d2 = LoginResponseDto.builder().token("tok2").build();
            assertThat(d1).isNotEqualTo(d2);
        }

        @Test
        void shouldNotBeEqualWhenComparedWithNull() {
            LoginResponseDto d = LoginResponseDto.builder().token("tok").build();
            assertThat(d).isNotEqualTo(null);
        }

        @Test
        void shouldNotBeEqualWhenComparedWithDifferentType() {
            LoginResponseDto d = LoginResponseDto.builder().token("tok").build();
            assertThat(d).isNotEqualTo(123);
        }
    }

    @Nested
    class ToString {
        @Test
        void shouldContainToken() {
            LoginResponseDto d = LoginResponseDto.builder().token("jwt-token").build();
            assertThat(d.toString()).contains("jwt-token");
        }
    }

    @Nested
    class Getters {
        @Test
        void shouldReturnToken() {
            LoginResponseDto d = new LoginResponseDto("tok");
            assertThat(d.getToken()).isEqualTo("tok");
        }
    }

    @Nested
    class Setters {
        @Test
        void shouldSetToken() {
            LoginResponseDto d = new LoginResponseDto();
            d.setToken("new-tok");
            assertThat(d.getToken()).isEqualTo("new-tok");
        }
    }

    @Nested
    class NoArgsConstructor {
        @Test
        void shouldCreateEmptyDto() {
            LoginResponseDto d = new LoginResponseDto();
            assertThat(d).isNotNull();
        }
    }

    @Nested
    class AllArgsConstructor {
        @Test
        void shouldCreateDtoWithToken() {
            LoginResponseDto d = new LoginResponseDto("tok");
            assertThat(d.getToken()).isEqualTo("tok");
        }
    }

    @Nested
    class Builder {
        @Test
        void shouldBuildDto() {
            LoginResponseDto d = LoginResponseDto.builder().token("tok").build();
            assertThat(d.getToken()).isEqualTo("tok");
        }
    }
}
