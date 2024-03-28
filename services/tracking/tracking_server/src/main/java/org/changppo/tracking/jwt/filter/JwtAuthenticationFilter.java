package org.changppo.tracking.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenValue = resolveToken(request);

        if(tokenValue != null) {
            JwtAuthenticationToken token = new JwtAuthenticationToken(tokenValue); // 인증되지 않은 토큰

            try {
                Authentication authentication = this.authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException e) { // server error
                log.error("JWT({})로부터 인증정보를 만드는데 실패했습니다: {}", token, e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
