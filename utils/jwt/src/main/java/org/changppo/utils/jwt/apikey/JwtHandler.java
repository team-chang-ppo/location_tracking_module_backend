package org.changppo.utils.jwt.apikey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.changppo.utils.jwt.TokenKeyProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@EnableConfigurationProperties(TokenKeyProperties.class)
public class JwtHandler {

    private final SecretKey secretKey;
    private static final String ID = "ID";
    private static final String MEMBER_ID = "MEMBER_ID";
    private static final String GRADE_TYPE = "GRADE_TYPE";

    public JwtHandler(TokenKeyProperties tokenKeyProperties) {
        secretKey = new SecretKeySpec(tokenKeyProperties.getSecret().getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String createToken(TokenClaims tokenClaims) {
        return create(Map.of(ID, tokenClaims.getId(), MEMBER_ID, tokenClaims.getMemberId(), GRADE_TYPE, tokenClaims.getGradeType()));
    }

    private String create(Map<String, Object> tokenClaims) {
        Date now = new Date();
        return Jwts.builder()
                .claims(tokenClaims)
                .issuedAt(now)
                .signWith(secretKey)
                .compact();
    }

    public Optional<TokenClaims> parseToken(String token) {
        return parse(token).map(this::convert);
    }

    private TokenClaims convert(Claims claims) {
        return new TokenClaims(
                claims.get(ID, Long.class),
                claims.get(MEMBER_ID, Long.class),
                claims.get(GRADE_TYPE, String.class)
        );
    }

    private Optional<Claims> parse(String token) {
        try {
            return Optional.of(
                    Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload());
        } catch (JwtException e) {
            log.warn("JWT error - {}", e.getMessage());
            return Optional.empty();
        }
    }
}
