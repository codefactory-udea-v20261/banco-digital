package com.udea.bancodigital.shared.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApiResponse")
class ApiResponseTest {
    @Nested
    @DisplayName("ok(data)")
    class OkDataTest {

        @Test
        @DisplayName("Debe crear respuesta exitosa con data y sin error")
        void debeCrearRespuestaExitosa() {
            ApiResponse<String> response = ApiResponse.ok("resultado");

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getData()).isEqualTo("resultado");
            assertThat(response.getError()).isNull();
            assertThat(response.getMessage()).isNull();
            assertThat(response.getTimestamp()).isNotNull();
        }
    }

    @Nested
    @DisplayName("ok(message, data)")
    class OkMessageDataTest {

        @Test
        @DisplayName("Debe crear respuesta exitosa con mensaje y data")
        void debeCrearRespuestaConMensaje() {
            ApiResponse<String> response = ApiResponse.ok("Operación exitosa", "resultado");

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Operación exitosa");
            assertThat(response.getData()).isEqualTo("resultado");
            assertThat(response.getError()).isNull();
        }
    }

    @Nested
    @DisplayName("created(data)")
    class CreatedTest {

        @Test
        @DisplayName("Debe crear respuesta con mensaje de recurso creado")
        void debeCrearRespuestaCreado() {
            ApiResponse<String> response = ApiResponse.created("nuevo");

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Recurso creado exitosamente");
            assertThat(response.getData()).isEqualTo("nuevo");
        }
    }

    @Nested
    @DisplayName("error(apiError)")
    class ErrorTest {

        @Test
        @DisplayName("Debe crear respuesta de error con success=false")
        void debeCrearRespuestaDeError() {
            ApiError apiError = ApiError.builder()
                    .errorCode("ERR_001")
                    .message("Error de validación")
                    .httpStatus(400)
                    .build();

            ApiResponse<Void> response = ApiResponse.error(apiError);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getError()).isEqualTo(apiError);
            assertThat(response.getData()).isNull();
        }
    }

}
