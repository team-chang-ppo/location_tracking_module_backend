package org.changppo.monitoring.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.changppo.commons.FailedResponseBody;
import org.changppo.monioring.domain.error.ErrorCode;
import org.changppo.monioring.domain.error.RemoteSessionFetchFailedException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;


@Setter
@RequiredArgsConstructor
public class RemoteSessionAuthenticationFilter extends OncePerRequestFilter {
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final RemoteSessionRetrieveStrategy remoteSessionRetrieveStrategy;
    private final SessionQueryProperties sessionQueryProperties;
    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 세션 값 있으면, 조회 시도

        final RemoteSessionAuthentication sessionInfo;
        try {
            sessionInfo = getSessionInfo(request);
            if (sessionInfo != null) {
                SecurityContext context = securityContextHolderStrategy.createEmptyContext();
                context.setAuthentication(sessionInfo);
                securityContextHolderStrategy.setContext(context);
            }
            filterChain.doFilter(request, response);
        } catch (RemoteSessionFetchFailedException e) {
            ErrorCode errorCode = e.getErrorCode();
            FailedResponseBody<?> failedResponseBody = errorCode.toResponse();
            response.setStatus(errorCode.getResponseStatus());
            response.getWriter().write(objectMapper.writeValueAsString(failedResponseBody));
        }

    }

    protected RemoteSessionAuthentication getSessionInfo(HttpServletRequest request) throws RemoteSessionFetchFailedException {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        String sessionId = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(sessionQueryProperties.getSessionCookieName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (sessionId == null) {
            return null;
        }
        return remoteSessionRetrieveStrategy.retrieve(sessionId);
    }
}
