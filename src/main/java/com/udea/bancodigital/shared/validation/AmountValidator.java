package com.udea.bancodigital.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

/**
 * Validator for monetary amounts.
 */
public class AmountValidator implements ConstraintValidator<ValidAmount, Object> {

    @Override
    public void initialize(ValidAmount annotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;  // null is handled by @NotNull
        }
        
        if (value instanceof String str) {
            if (str.isBlank()) {
                return false;
            }
            try {
                BigDecimal amount = new BigDecimal(str);
                // Check positive and up to 2 decimals
                return amount.signum() > 0 && amount.scale() <= 2;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (value instanceof BigDecimal decimal) {
            return decimal.signum() > 0 && decimal.scale() <= 2;
        } else if (value instanceof Number num) {
            return num.doubleValue() > 0;
        }
        
        return false;
    }
}
