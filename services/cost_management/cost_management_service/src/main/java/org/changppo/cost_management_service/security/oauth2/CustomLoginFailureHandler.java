package org.changppo.cost_management_service.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.response.exception.common.ResponseHandler;
import org.changppo.cost_management_service.response.Response;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.changppo.cost_management_service.response.exception.common.ExceptionType.KAKAO_LOGIN_OAUTH2_FAILURE_EXCEPTION;

@Component
@RequiredArgsConstructor
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    private final ResponseHandler responseHandler;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(KAKAO_LOGIN_OAUTH2_FAILURE_EXCEPTION.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(convertToJson(responseHandler.getFailureResponse(KAKAO_LOGIN_OAUTH2_FAILURE_EXCEPTION)));
    }

    private String convertToJson(Response response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(response);
    }
}