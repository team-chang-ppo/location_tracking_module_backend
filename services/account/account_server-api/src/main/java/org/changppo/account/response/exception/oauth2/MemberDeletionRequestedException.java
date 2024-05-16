package org.changppo.account.response.exception.oauth2;
import org.springframework.security.core.AuthenticationException;

public class MemberDeletionRequestedException extends AuthenticationException {
    public MemberDeletionRequestedException(String message) {
        super(message);
    }
}
