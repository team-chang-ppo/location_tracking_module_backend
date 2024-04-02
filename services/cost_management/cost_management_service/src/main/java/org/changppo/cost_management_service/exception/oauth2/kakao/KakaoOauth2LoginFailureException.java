package org.changppo.cost_management_service.exception.oauth2.kakao;
import org.springframework.security.core.AuthenticationException;

public class KakaoOauth2LoginFailureException extends AuthenticationException {
    public KakaoOauth2LoginFailureException(String message) {
        super(message);
    }
}