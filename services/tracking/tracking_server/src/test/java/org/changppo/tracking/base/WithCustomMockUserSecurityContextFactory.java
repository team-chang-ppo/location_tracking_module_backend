package org.changppo.tracking.base;

import org.changppo.tracking.domain.TrackingContext;
import org.changppo.tracking.jwt.JwtAuthentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithCustomMockUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {
    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
        String trackingId = annotation.trackingId();
        String authority = annotation.authority();

        JwtAuthentication token = new JwtAuthentication(trackingId, authority);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        return context;
    }
}
