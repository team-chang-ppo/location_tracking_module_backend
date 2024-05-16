package org.changppo.account.security.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.changppo.account.response.exception.oauth2.MemberDeletionRequestedException;
import org.changppo.account.response.exception.oauth2.Oauth2LoginFailureException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if (authException instanceof MemberDeletionRequestedException) {
            setDefaultFailureUrl("/login?error=member-deletion");
        } else if (authException instanceof Oauth2LoginFailureException) {
            setDefaultFailureUrl("/login?error=oauth-failure");
        } else {
            setDefaultFailureUrl("/login?error=general");
        }
        super.onAuthenticationFailure(request, response, authException);
    }
}
