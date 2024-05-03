package org.changppo.account.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.changppo.account.security.PrincipalHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class ContextInjectionAspect {

    @Before("@annotation(org.changppo.account.aop.AssignMemberId)")
    public void injectMemberId(JoinPoint joinPoint) {
        injectContext(joinPoint);
    }

    private void injectContext(JoinPoint joinPoint) {
        Arrays.stream(joinPoint.getArgs())
                .forEach(arg -> {
                    if (arg instanceof AssignMemberId) {
                        ((AssignMemberId) arg).setMemberId(PrincipalHandler.extractId());
                    }
                });
    }

    public interface AssignMemberId{
        void setMemberId(Long memberId);
    }
}