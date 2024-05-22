package org.changppo.monitoring.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.changppo.commons.FailedResponseBody;
import org.changppo.monioring.domain.error.ErrorCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final static String AUTHENTICATION_REQUIRED_MESSAGE;
    static {
        ObjectMapper objectMapper = new ObjectMapper();
        FailedResponseBody<?> response = ErrorCode.VALID_AUTHENTICATION_REQUIRED.toResponse();
        try {
            AUTHENTICATION_REQUIRED_MESSAGE = objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.debug("403 - Authentication Required");
        int status = ErrorCode.VALID_AUTHENTICATION_REQUIRED.getResponseStatus();
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(AUTHENTICATION_REQUIRED_MESSAGE);
    }
}
