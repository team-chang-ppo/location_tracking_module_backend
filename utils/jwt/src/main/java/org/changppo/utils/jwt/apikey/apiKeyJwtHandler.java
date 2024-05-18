package org.changppo.utils.jwt.apikey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.changppo.utils.jwt.JwtProperties;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class apiKeyJwtHandler {

    private final SecretKey secretKey;
    private static final String APIKEY_ID = "APIKEY_ID";
    private static final String MEMBER_ID = "MEMBER_ID";
    private static final String GRADE_TYPE = "GRADE_TYPE";

    public apiKeyJwtHandler(JwtProperties jwtProperties) {
        secretKey = new SecretKeySpec(jwtProperties.getApiKey().getSecretKey().getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String createToken(apiKeyJwtClaims apiKeyJwtClaims) {
        return create(Map.of(APIKEY_ID, apiKeyJwtClaims.getApikeyId(), MEMBER_ID, apiKeyJwtClaims.getMemberId(), GRADE_TYPE, apiKeyJwtClaims.getGradeType()));
    }

    private String create(Map<String, Object> tokenClaims) {
        Date now = new Date();
        return Jwts.builder()
                .claims(tokenClaims)
                .issuedAt(now)
                .signWith(secretKey)
                .compact();
    }

    public Optional<apiKeyJwtClaims> parseToken(String token) {  // 반환 값이 Optional.empty면 오류
        return parse(token).map(this::convert);
    }

    private apiKeyJwtClaims convert(Claims claims) {
        return new apiKeyJwtClaims(
                claims.get(APIKEY_ID, Long.class),
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
