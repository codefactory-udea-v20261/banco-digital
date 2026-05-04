package com.udea.bancodigital.shared.exception;

import com.udea.bancodigital.shared.web.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void handleAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Denied");
        ResponseEntity<ApiResponse<Void>> response = handler.handleAccessDeniedException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getError().getErrorCode()).isEqualTo("FORBIDDEN");
    }

    @Test
    void handleBadCredentialsException() {
        BadCredentialsException ex = new BadCredentialsException("Bad creds");
        ResponseEntity<ApiResponse<Void>> response = handler.handleBadCredentialsException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getError().getErrorCode()).isEqualTo("UNAUTHORIZED");
    }

    @Test
    void handleBusinessException() {
        BusinessException ex = new BusinessException("TEST_ERROR", "Test message", HttpStatus.BAD_REQUEST) {};
        ResponseEntity<ApiResponse<Void>> response = handler.handleBusinessException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError().getErrorCode()).isEqualTo("TEST_ERROR");
    }

    @Test
    void handleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid arg");
        ResponseEntity<ApiResponse<Void>> response = handler.handleIllegalArgumentException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError().getErrorCode()).isEqualTo("INVALID_ARGUMENT");
    }

    @Test
    void handleGenericException() {
        Exception ex = new Exception("Unknown");
        ResponseEntity<ApiResponse<Void>> response = handler.handleGenericException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getError().getErrorCode()).isEqualTo("INTERNAL_SERVER_ERROR");
    }

    @Test
    void handleKafkaListenerException() {
        ListenerExecutionFailedException ex = new ListenerExecutionFailedException("Kafka error");
        ResponseEntity<ApiResponse<Void>> response = handler.handleKafkaListenerException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getError().getErrorCode()).isEqualTo("MESSAGE_PROCESSING_ERROR");
    }
}
