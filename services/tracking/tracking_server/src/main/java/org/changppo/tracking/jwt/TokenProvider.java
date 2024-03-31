package org.changppo.tracking.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.tracking.config.JwtProperties;
import org.changppo.tracking.domain.TrackingContext;
import org.changppo.tracking.jwt.exception.JwtTokenExpiredException;
import org.changppo.tracking.jwt.exception.JwtTokenInvalidException;
import org.changppo.tracking.jwt.filter.JwtAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;


@Slf4j
@RequiredArgsConstructor
@Component
public class TokenProvider implements AuthenticationProvider {
    private static final long MILLI_SECOND = 1000L;
    private static final String AUTHORITY = "Authority";

    @Autowired
    private JwtProperties jwtProperties;

    public String createToken(TrackingContext context) {
        Claims claims = Jwts.claims().setSubject(context.trackingId());
        claims.put(AUTHORITY, context.authority());
        Date now = new Date(System.currentTimeMillis());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtProperties.getTokenExpirySeconds()*MILLI_SECOND))
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public TrackingContext parseToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return new TrackingContext(claims.getSubject(), (String) claims.get(AUTHORITY));
        } catch (ExpiredJwtException e) {
            throw new JwtTokenExpiredException(e);
        } catch (Exception e) {
            throw new JwtTokenInvalidException(e);
        }
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String tokenValue = jwtAuthenticationToken.token();
        if (tokenValue == null) {
            return null;
        }
        TrackingContext context = this.parseToken(tokenValue);
        return new JwtAuthentication(context); // 인증된 토큰 반환
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
