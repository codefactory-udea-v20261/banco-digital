package com.udea.bancodigital.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates document number format (Colombian cedula or passport).
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DocumentNumberValidator.class)
public @interface ValidDocumentNumber {
    String message() default "Invalid document number format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
