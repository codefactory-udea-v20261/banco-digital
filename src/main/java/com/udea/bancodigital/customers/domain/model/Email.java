package com.udea.bancodigital.customers.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object para el email del cliente.
 * 
 * REGLAS DE NEGOCIO:
 * - Debe tener formato válido de email
 * - No puede exceder 255 caracteres
 * - Es inmutable
 * - La validación está en el dominio
 */
public record Email(String valor) {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final int MAX_LENGTH = 255;
    
    public Email {
        validar(valor);
    }
    
    private void validar(String valor) {
        Objects.requireNonNull(valor, "El email no puede ser nulo");
        
        if (valor.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        
        if (valor.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                "El email no puede exceder " + MAX_LENGTH + " caracteres"
            );
        }
        
        if (!EMAIL_PATTERN.matcher(valor).matches()) {
            throw new IllegalArgumentException(
                "El email no tiene un formato válido: " + valor
            );
        }
    }
    
    /**
     * Factory method para crear desde String.
     * Útil cuando se lee desde la BD o DTOs.
     */
    public static Email of(String valor) {
        return new Email(valor);
    }
    
    @Override
    public String toString() {
        return valor;
    }
}
