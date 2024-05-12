package org.changppo.monitoring.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Aspect
//@Order(Ordered.HIGHEST_PRECEDENCE) // 가장 먼저 검증하도록
@Component
@RequiredArgsConstructor
public class ValidatedArgsAspect {
    private final ValidatorFactory validatorFactory;

    @Around("@annotation(validatedArgs)")
    public Object aroundValidatedArgs(ProceedingJoinPoint joinPoint, ValidatedArgs validatedArgs) throws Throwable {

        // annotation 에서 설정한 값 가져오기
        final boolean nullable = validatedArgs.nullable();
        final String message = validatedArgs.message();

        final Object[] args = joinPoint.getArgs();
        final Validator validator = validatorFactory.getValidator();
        final Set<ConstraintViolation<Object>> constraintViolations = new HashSet<>();

        for (Object arg : args) {
            if (arg == null) {
                if (!nullable) {
                    throw new IllegalArgumentException("Argument must not be null");
                }
                continue;
            }
            constraintViolations.addAll(validator.validate(arg));
        }

        if (!constraintViolations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(message).append(" : ");
            for (ConstraintViolation<Object> violation : constraintViolations) {
                sb.append(violation.getMessage()).append(" , ");
            }
            throw new IllegalArgumentException(sb.toString());
        }

        return joinPoint.proceed();
    }

}
