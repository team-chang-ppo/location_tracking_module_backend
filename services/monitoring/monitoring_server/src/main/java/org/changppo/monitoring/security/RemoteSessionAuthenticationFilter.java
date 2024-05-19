package org.changppo.monitoring.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Setter
@RequiredArgsConstructor
public class RemoteSessionAuthenticationFilter extends OncePerRequestFilter {
    private final static String SESSION_ID = "JSESSIONID";
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final RemoteSessionRetrieveStrategy remoteSessionRetrieveStrategy;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 세션 값 있으면, 조회 시도
        RemoteSessionAuthentication sessionInfo = getSessionInfo(request);
        if (sessionInfo != null) {
            SecurityContext context = securityContextHolderStrategy.createEmptyContext();
            context.setAuthentication(sessionInfo);
            securityContextHolderStrategy.setContext(context);
        }
        filterChain.doFilter(request, response);
    }

    protected RemoteSessionAuthentication getSessionInfo(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        String sessionId = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(SESSION_ID))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (sessionId == null) {
            return null;
        }

        return remoteSessionRetrieveStrategy.retrieve(sessionId);
    }
}
