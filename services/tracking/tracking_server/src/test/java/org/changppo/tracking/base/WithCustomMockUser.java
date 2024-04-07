package org.changppo.tracking.base;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomMockUserSecurityContextFactory.class)
public @interface WithCustomMockUser {
    String trackingId() default "trackingId";
    String apiKeyId() default "apiKeyId";
    String[] scopes() default {"READ_TRACKING_COORDINATE", "WRITE_TRACKING_COORDINATE", "ACCESS_TRACKING_HISTORY"};
}