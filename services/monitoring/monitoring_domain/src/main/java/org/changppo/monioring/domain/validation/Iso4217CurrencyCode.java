package org.changppo.monioring.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = Iso4217CurrencyCodeValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Iso4217CurrencyCode {
    String message() default "Currency code is not ISO 4217 standard.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
