package org.changppo.tracking.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.tracking.domain.TrackingContext;
import org.changppo.tracking.jwt.exception.JwtTokenExpiredException;
import org.changppo.tracking.jwt.exception.JwtTokenInvalidException;
import org.changppo.tracking.jwt.filter.JwtAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Component
public class TokenProvider implements AuthenticationProvider {
    private static final long MILLI_SECOND = 1000L;
    private static final String SCOPE = "scope";
    private static final String API_KEY_ID = "api-key-id";

    private final JwtProperties jwtProperties;

    public String createToken(TrackingContext context, Long tokenExpiresIn) {
        Claims claims = this.createClaims(context);
        Date now = new Date(System.currentTimeMillis());

        return Jwts.builder()
                .setIssuer(jwtProperties.getIssuer())
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenExpiresIn*MILLI_SECOND))
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims createClaims(TrackingContext context) {
        Claims claims = Jwts.claims().setSubject(context.trackingId());
        claims.put(SCOPE, context.scopes());
        claims.put(API_KEY_ID, context.apiKeyId());
        return claims;
    }

    public TrackingContext parseToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return new TrackingContext(
                    claims.getSubject(),
                    claims.get(API_KEY_ID, String.class),
                    claims.get(SCOPE, List.class));
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