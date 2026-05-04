package com.udea.bancodigital.shared.exception;

import com.udea.bancodigital.shared.web.ApiError;
import com.udea.bancodigital.shared.web.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manejador global de excepciones.
 * Garantiza que TODAS las APIs devuelvan una estructura uniforme ApiResponse<T>.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("[{}] AccessDeniedException: {} — path={}", traceId, ex.getMessage(), request.getRequestURI());
        ApiError error = ApiError.builder()
                .errorCode("FORBIDDEN")
                .message("No tiene permisos para acceder a este recurso")
                .traceId(traceId)
                .httpStatus(HttpStatus.FORBIDDEN.value())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(error));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(
            BadCredentialsException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("[{}] BadCredentialsException: {} — path={}", traceId, ex.getMessage(), request.getRequestURI());
        ApiError error = ApiError.builder()
                .errorCode("UNAUTHORIZED")
                .message("Credenciales inválidas")
                .traceId(traceId)
                .httpStatus(HttpStatus.UNAUTHORIZED.value())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(error));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("[{}] BusinessException: {} — path={}", traceId, ex.getMessage(), request.getRequestURI());
        ApiError error = ApiError.builder()
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .traceId(traceId)
                .httpStatus(ex.getHttpStatus().value())
                .build();
        return ResponseEntity.status(ex.getHttpStatus()).body(ApiResponse.error(error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        List<String> details = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        log.warn("[{}] ValidationException: {} — path={}", traceId, details, request.getRequestURI());
        ApiError error = ApiError.builder()
                .errorCode("VALIDATION_ERROR")
                .message("Los datos enviados no son válidos")
                .details(details)
                .traceId(traceId)
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .build();
        return ResponseEntity.badRequest().body(ApiResponse.error(error));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        List<String> details = ex.getConstraintViolations()
                .stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .toList();
        log.warn("[{}] ConstraintViolationException: {} — path={}", traceId, details, request.getRequestURI());
        ApiError error = ApiError.builder()
                .errorCode("VALIDATION_ERROR")
                .message("Validación de restricciones fallida")
                .details(details)
                .traceId(traceId)
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .build();
        return ResponseEntity.badRequest().body(ApiResponse.error(error));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        List<String> details = new ArrayList<>();
        details.add(ex.getName() + " tiene tipo inválido: esperado " + ex.getRequiredType().getSimpleName());
        log.warn("[{}] TypeMismatchException: {} — path={}", traceId, ex.getMessage(), request.getRequestURI());
        ApiError error = ApiError.builder()
                .errorCode("INVALID_PARAMETER_TYPE")
                .message("Tipo de parámetro inválido")
                .details(details)
                .traceId(traceId)
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .build();
        return ResponseEntity.badRequest().body(ApiResponse.error(error));
    }

    @ExceptionHandler(ListenerExecutionFailedException.class)
    public ResponseEntity<ApiResponse<Void>> handleKafkaListenerException(
            ListenerExecutionFailedException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("[{}] Kafka listener failed: {} — path={}", traceId, ex.getMessage(), request.getRequestURI(), ex);
        ApiError error = ApiError.builder()
                .errorCode("MESSAGE_PROCESSING_ERROR")
                .message("Error procesando mensaje del evento")
                .traceId(traceId)
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
        return ResponseEntity.internalServerError().body(ApiResponse.error(error));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("[{}] IllegalArgumentException: {} — path={}", traceId, ex.getMessage(), request.getRequestURI());
        ApiError error = ApiError.builder()
                .errorCode("INVALID_ARGUMENT")
                .message("Argumento inválido: " + ex.getMessage())
                .traceId(traceId)
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .build();
        return ResponseEntity.badRequest().body(ApiResponse.error(error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("[{}] Unexpected error — path={}", traceId, request.getRequestURI(), ex);
        ApiError error = ApiError.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .message("Ocurrió un error inesperado. Contacte al administrador.")
                .traceId(traceId)
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
        return ResponseEntity.internalServerError().body(ApiResponse.error(error));
    }

    private String generateTraceId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
