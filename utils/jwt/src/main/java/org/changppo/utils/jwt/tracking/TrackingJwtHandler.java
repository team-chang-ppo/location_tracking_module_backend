package org.changppo.utils.jwt.tracking;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.changppo.utils.jwt.JwtProperties;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TrackingJwtHandler {
    private final SecretKey secretKey;
    private final JwtProperties jwtProperties;
    private static final long MILLI_SECOND = 1000L;
    private static final String APIKEY_ID = "APIKEY_ID";
    private static final String MEMBER_ID = "MEMBER_ID";
    private static final String GRADE_TYPE = "GRADE_TYPE";
    private static final String TRACKING_ID = "TRACKING_ID";
    private static final String SCOPE = "SCOPE";

    public TrackingJwtHandler(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        secretKey = new SecretKeySpec(jwtProperties.getTrackingToken().getSecretKey().getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String createToken(TrackingJwtClaims trackingClaims) {
        Map<String, Object> tokenClaims = this.createClaims(trackingClaims);
        Date now = new Date(System.currentTimeMillis());

        return Jwts.builder()
                .issuer(jwtProperties.getTrackingToken().getIssuer())
                .claims(tokenClaims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtProperties.getTrackingToken().getExpireIn() * MILLI_SECOND))
                .signWith(secretKey)
                .compact();
    }

    public Map<String, Object> createClaims(TrackingJwtClaims trackingClaims) {
        return Map.of(
                APIKEY_ID, trackingClaims.getApikeyId(),
                MEMBER_ID, trackingClaims.getMemberId(),
                GRADE_TYPE, trackingClaims.getGradeType(),
                TRACKING_ID, trackingClaims.getTrackingId(),
                SCOPE, trackingClaims.getScopes()
                );
    }

    // exception은 사용하는 곳에서 처리
    public TrackingJwtClaims parseToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return this.convert(claims);
    }

    public TrackingJwtClaims convert(Claims claims) {
        return new TrackingJwtClaims(
                claims.get(APIKEY_ID, Long.class),
                claims.get(MEMBER_ID, Long.class),
                claims.get(GRADE_TYPE, String.class),
                claims.get(TRACKING_ID, String.class),
                claims.get(SCOPE, List.class));
    }
}
