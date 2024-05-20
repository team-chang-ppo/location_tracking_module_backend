package org.changppo.account.response.exception.oauth2;
import org.springframework.security.core.AuthenticationException;

public class AdminBannedException extends AuthenticationException {
    public AdminBannedException(String message) {
        super(message);
    }
}
