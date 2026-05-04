package com.udea.bancodigital.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for document numbers (Colombian cedula or passport).
 */
public class DocumentNumberValidator implements ConstraintValidator<ValidDocumentNumber, String> {

    @Override
    public void initialize(ValidDocumentNumber annotation) {
        // No initialization required for this validator
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value.isBlank()) {
            return false;
        }
        // Accept 6-20 alphanumeric characters
        return value.matches("^[A-Za-z\\d]{6,20}$");
    }
}
