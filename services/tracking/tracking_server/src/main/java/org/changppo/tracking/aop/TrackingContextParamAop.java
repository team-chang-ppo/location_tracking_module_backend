package org.changppo.tracking.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;
import org.changppo.tracking.domain.TrackingContext;
import org.changppo.tracking.exception.RequiredAuthenticationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class TrackingContextParamAop {

    @Around("@annotation(org.changppo.tracking.aop.TrackingContextParam)")
    public Object getTrackingContext(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            log.error("인증이 필요한 접근입니다. 현재 인증 정보(Principal: {}, Authorities: {})",
                    authentication.getPrincipal(), authentication.getAuthorities());
            throw new RequiredAuthenticationException();
        }

        TrackingContext context = (TrackingContext)authentication.getPrincipal();
        Object[] modifiedArgs = modifyArgsWithTrackingContext(context, proceedingJoinPoint);

        return proceedingJoinPoint.proceed(modifiedArgs);
    }

    private Object[] modifyArgsWithTrackingContext(TrackingContext trackingContext, ProceedingJoinPoint proceedingJoinPoint) {
        Object[] parameters = proceedingJoinPoint.getArgs();
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] methodParameters = method.getParameters();

        for (int i = 0; i < methodParameters.length; i++) {
            Parameter parameter = methodParameters[i];
            if (parameter.getType().equals(TrackingContext.class)) {
                parameters[i] = trackingContext;
                break;
            }
        }

        return parameters;
    }
}
