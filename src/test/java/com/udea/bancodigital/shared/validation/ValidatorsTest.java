package com.udea.bancodigital.shared.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ValidatorsTest {

    @Mock
    private ConstraintValidatorContext context;

    private AmountValidator amountValidator;
    private AccountNumberValidator accountNumberValidator;
    private DocumentNumberValidator documentNumberValidator;
    private PhoneNumberValidator phoneNumberValidator;

    @BeforeEach
    void setUp() {
        amountValidator = new AmountValidator();
        amountValidator.initialize(null);
        
        accountNumberValidator = new AccountNumberValidator();
        accountNumberValidator.initialize(null);
        
        documentNumberValidator = new DocumentNumberValidator();
        documentNumberValidator.initialize(null);
        
        phoneNumberValidator = new PhoneNumberValidator();
        phoneNumberValidator.initialize(null);
    }

    @Test
    void testAmountValidator() {
        assertThat(amountValidator.isValid(null, context)).isTrue();
        assertThat(amountValidator.isValid("100.50", context)).isTrue();
        assertThat(amountValidator.isValid("100.501", context)).isFalse(); // more than 2 decimals
        assertThat(amountValidator.isValid("-10", context)).isFalse();
        assertThat(amountValidator.isValid("", context)).isFalse();
        assertThat(amountValidator.isValid("invalid", context)).isFalse();
        
        assertThat(amountValidator.isValid(new BigDecimal("100.50"), context)).isTrue();
        assertThat(amountValidator.isValid(new BigDecimal("-10"), context)).isFalse();
        assertThat(amountValidator.isValid(new BigDecimal("100.501"), context)).isFalse();
        
        assertThat(amountValidator.isValid(100, context)).isTrue();
        assertThat(amountValidator.isValid(-10, context)).isFalse();
        
        assertThat(amountValidator.isValid(new Object(), context)).isFalse();
    }

    @Test
    void testAccountNumberValidator() {
        assertThat(accountNumberValidator.isValid(null, context)).isTrue();
        assertThat(accountNumberValidator.isValid("CO1234567890123", context)).isTrue(); 
        assertThat(accountNumberValidator.isValid("12345678901234567890", context)).isFalse();
        assertThat(accountNumberValidator.isValid("12345", context)).isFalse();
        assertThat(accountNumberValidator.isValid("abc", context)).isFalse();
        assertThat(accountNumberValidator.isValid("", context)).isFalse();
    }

    @Test
    void testDocumentNumberValidator() {
        assertThat(documentNumberValidator.isValid(null, context)).isTrue();
        assertThat(documentNumberValidator.isValid("123456", context)).isTrue();
        assertThat(documentNumberValidator.isValid("12345", context)).isFalse();
        assertThat(documentNumberValidator.isValid("abc", context)).isFalse();
        assertThat(documentNumberValidator.isValid("", context)).isFalse();
    }

    @Test
    void testPhoneNumberValidator() {
        assertThat(phoneNumberValidator.isValid(null, context)).isTrue(); // Let's update PhoneNumberValidator to return true for null
        assertThat(phoneNumberValidator.isValid("+573001234567", context)).isTrue();
        assertThat(phoneNumberValidator.isValid("03001234567", context)).isTrue();
        assertThat(phoneNumberValidator.isValid("3001234567", context)).isFalse();
        assertThat(phoneNumberValidator.isValid("abc", context)).isFalse();
        assertThat(phoneNumberValidator.isValid("", context)).isFalse();
    }
}
