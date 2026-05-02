package com.udea.bancodigital.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for Colombian phone numbers.
 */
public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    @Override
    public void initialize(ValidPhoneNumber annotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        // Accept +57XXXXXXXXX or 0XXXXXXXXXX (9-10 digits)
        return value.matches("^(\\+57|0)[0-9]{9,10}$");
    }
}
