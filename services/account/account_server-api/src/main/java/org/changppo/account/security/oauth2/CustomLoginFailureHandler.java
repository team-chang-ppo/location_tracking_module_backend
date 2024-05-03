package org.changppo.account.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.changppo.account.response.Response;
import org.changppo.account.response.exception.common.ResponseHandler;
import org.changppo.account.response.exception.oauth2.MemberDeletionRequestedException;
import org.changppo.account.response.exception.oauth2.Oauth2LoginFailureException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.changppo.account.response.exception.common.ExceptionType.*;

@Component
@RequiredArgsConstructor
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    private final ResponseHandler responseHandler;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(AUTHENTICATION_ENTRY_POINT_EXCEPTION.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        if (authException instanceof MemberDeletionRequestedException) {
            response.getWriter().write(convertToJson(responseHandler.getFailureResponse(MEMBER_DELETION_REQUESTED_EXCEPTION)));
        } else if (authException instanceof Oauth2LoginFailureException) {
            response.getWriter().write(convertToJson(responseHandler.getFailureResponse(OAUTH2_LOGIN_FAILURE_EXCEPTION)));
        } else {
            response.getWriter().write(convertToJson(responseHandler.getFailureResponse(AUTHENTICATION_ENTRY_POINT_EXCEPTION)));
        }
    }

    private String convertToJson(Response response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(response);
    }
}
