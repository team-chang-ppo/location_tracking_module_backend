package org.changppo.monioring.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Currency;

/**
 * 화폐 코드 (ISO 4217) 가 유효한지 검증합니다.<br>
 * null 은 유효한 값으로 간주합니다.
 */
public class Iso4217CurrencyCodeValidator implements ConstraintValidator<Iso4217CurrencyCode, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            Currency.getInstance(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
