package com.udea.bancodigital.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for account numbers (IBAN format).
 */
public class AccountNumberValidator implements ConstraintValidator<ValidAccountNumber, String> {

    @Override
    public void initialize(ValidAccountNumber annotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        // IBAN-style: 2 letters + 2 digits + alphanumeric (15-34 chars total)
        return value.matches("^[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}$") &&
               value.length() >= 15 && value.length() <= 34;
    }
}
