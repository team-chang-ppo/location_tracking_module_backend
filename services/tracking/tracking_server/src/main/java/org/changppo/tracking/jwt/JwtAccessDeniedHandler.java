package org.changppo.tracking.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.changppo.tracking.exception.common.ErrorCode;
import org.changppo.utils.response.body.Response;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        this.handleAccessDenied(response);
    }

    private void handleAccessDenied(HttpServletResponse response) throws IOException {
        response.setStatus(ErrorCode.ACCESS_DENIED.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ObjectMapper objectMapper = new ObjectMapper();
        String errorResponse = objectMapper.writeValueAsString(Response.failure(ErrorCode.ACCESS_DENIED.getCode(), ErrorCode.ACCESS_DENIED.getMessage()));
        response.getWriter().write(errorResponse);
        response.flushBuffer();
        response.getWriter().close();
    }
}
