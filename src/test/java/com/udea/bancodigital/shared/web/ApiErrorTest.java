package com.udea.bancodigital.shared.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApiError")
class ApiErrorTest {
    @Test
    @DisplayName("Debe construir con todos los campos")
    void debeConstruirConTodosLosCampos() {
        ApiError error = ApiError.builder()
                .errorCode("ERR_001")
                .message("Error de validación")
                .details(List.of("Campo requerido", "Formato inválido"))
                .traceId("trace-123")
                .httpStatus(400)
                .build();

        assertThat(error.getErrorCode()).isEqualTo("ERR_001");
        assertThat(error.getMessage()).isEqualTo("Error de validación");
        assertThat(error.getDetails()).containsExactly("Campo requerido", "Formato inválido");
        assertThat(error.getTraceId()).isEqualTo("trace-123");
        assertThat(error.getHttpStatus()).isEqualTo(400);
    }

    @Test
    @DisplayName("Debe construir correctamente sin campos opcionales")
    void debeConstruirSinCamposOpcionales() {
        ApiError error = ApiError.builder()
                .errorCode("ERR_500")
                .message("Error interno")
                .httpStatus(500)
                .build();

        assertThat(error.getErrorCode()).isEqualTo("ERR_500");
        assertThat(error.getHttpStatus()).isEqualTo(500);
        assertThat(error.getDetails()).isNull();
        assertThat(error.getTraceId()).isNull();
    }

}
