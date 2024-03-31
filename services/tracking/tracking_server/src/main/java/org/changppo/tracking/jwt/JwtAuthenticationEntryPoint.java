package org.changppo.tracking.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.changppo.tracking.exception.common.ErrorResponse;
import org.changppo.tracking.jwt.exception.JwtAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if(response.isCommitted()) {
            return;
        }
        if(authException instanceof JwtAuthenticationException) {
            JwtAuthenticationException exception = (JwtAuthenticationException) authException;
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            String errorResponse = objectMapper.writeValueAsString(ErrorResponse.of(exception.getErrorCode(),null));
            response.getWriter().write(errorResponse);
            response.flushBuffer(); // 커밋
            response.getWriter().close();
        } else {
            // 기본적인 예외 처리
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
        }
    }
}
