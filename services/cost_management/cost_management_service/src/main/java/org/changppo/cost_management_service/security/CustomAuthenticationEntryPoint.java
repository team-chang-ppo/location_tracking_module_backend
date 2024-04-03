package org.changppo.cost_management_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.response.exception.common.ResponseHandler;
import org.changppo.cost_management_service.response.Response;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import static org.changppo.cost_management_service.response.exception.common.ExceptionType.AUTHENTICATION_ENTRY_POINT_EXCEPTION;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ResponseHandler responseHandler;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(AUTHENTICATION_ENTRY_POINT_EXCEPTION.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(convertToJson(responseHandler.getFailureResponse(AUTHENTICATION_ENTRY_POINT_EXCEPTION)));
    }

    private String convertToJson(Response response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(response);
    }
}