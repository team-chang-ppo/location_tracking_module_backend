package org.changppo.tracking.base;

import org.changppo.tracking.jwt.JwtAuthentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.List;

public class WithCustomMockUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {
    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
        String trackingId = annotation.trackingId();
        String apiKeyId = annotation.apiKeyId();
        List<String> scopes = Arrays.asList(annotation.scopes());

        JwtAuthentication token = new JwtAuthentication(trackingId, apiKeyId, scopes);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        return context;
    }
}