package org.changppo.tracking.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.tracking.domain.TrackingContext;
import org.changppo.tracking.jwt.exception.JwtTokenExpiredException;
import org.changppo.tracking.jwt.exception.JwtTokenInvalidException;
import org.changppo.tracking.jwt.filter.JwtAuthenticationToken;
import org.changppo.utils.jwt.tracking.TrackingJwtClaims;
import org.changppo.utils.jwt.tracking.TrackingJwtHandler;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;



@Slf4j
@RequiredArgsConstructor
@Component
public class TokenProvider implements AuthenticationProvider {

    private final TrackingJwtHandler trackingJwtHandler;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String tokenValue = jwtAuthenticationToken.token();
        if (tokenValue == null) {
            return null;
        }

        try{
            TrackingJwtClaims claims = trackingJwtHandler.parseToken(tokenValue);
            return new JwtAuthentication(claims); // 인증된 토큰 반환
        } catch (ExpiredJwtException e) {
            // TODO. API-KEY 의 상태를 확인하는 로직이 추가되어야 함.
            throw new JwtTokenExpiredException(e.getClaims());
        } catch (Exception e) {
            throw new JwtTokenInvalidException(e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}