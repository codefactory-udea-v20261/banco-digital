package com.udea.bancodigital.customers.domain.model;

import java.util.Objects;

/**
 * Value Object para el número de cédula del cliente.
 * 
 * REGLAS DE NEGOCIO:
 * - Debe tener entre 7 y 20 caracteres
 * - Solo puede contener dígitos
 * - Es inmutable
 * - La validación está en el dominio, no en DTOs
 */
public record NumeroCedula(String valor) {
    
    private static final String REGEX_CEDULA = "^\\d{7,20}$";
    
    public NumeroCedula {
        validar(valor);
    }
    
    private void validar(String valor) {
        Objects.requireNonNull(valor, "El número de cédula no puede ser nulo");
        
        if (valor.isBlank()) {
            throw new IllegalArgumentException("El número de cédula no puede estar vacío");
        }
        
        if (!valor.matches(REGEX_CEDULA)) {
            throw new IllegalArgumentException(
                "El número de cédula debe contener solo dígitos y tener entre 7 y 20 caracteres. Recibido: " + valor
            );
        }
    }
    
    /**
     * Factory method para crear desde String.
     * Útil cuando se lee desde la BD o DTOs.
     */
    public static NumeroCedula of(String valor) {
        return new NumeroCedula(valor);
    }
    
    @Override
    public String toString() {
        return valor;
    }
}
