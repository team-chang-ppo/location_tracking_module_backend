package org.changppo.tracking.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPointValidator.class)
public @interface ValidPoint {
    String message() default "Point cannot be empty";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}