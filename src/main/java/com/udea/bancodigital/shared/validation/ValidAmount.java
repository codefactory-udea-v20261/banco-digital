package com.udea.bancodigital.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates currency amount (positive, up to 2 decimals).
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = AmountValidator.class)
public @interface ValidAmount {
    String message() default "Invalid amount format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
