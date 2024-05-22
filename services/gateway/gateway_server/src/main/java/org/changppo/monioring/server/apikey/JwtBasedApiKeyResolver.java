package org.changppo.monioring.server.apikey;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.changppo.monioring.domain.GatewayConstant;
import org.changppo.monioring.server.exception.InvalidApiKeyException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class JwtBasedApiKeyResolver implements ApiKeyResolver {
    private static final String APIKEY_ID = "APIKEY_ID";
    private static final String MEMBER_ID = "MEMBER_ID";
    private static final String GRADE_TYPE = "GRADE_TYPE";
    private final JwtConfigurationProperty jwtConfigurationProperty;

    @Override
    public Mono<ApiKey> resolve(ServerWebExchange exchange) {

        final String token = exchange.getRequest().getHeaders().getFirst(GatewayConstant.API_KEY_HEADER);
        if (token == null) {
            return Mono.empty();
        }

        return Mono.defer(() -> {
            final DecodedJWT decodedJWT;
            try {
                decodedJWT = decode(token);
            } catch (InvalidApiKeyException e) {
                if (jwtConfigurationProperty.isDenyInvalidToken()) {
                    return Mono.error(e);
                } else {
                    return Mono.empty();
                }
            }
            ApiKey apiKey;
            try {
                Long memberId = decodedJWT.getClaim(MEMBER_ID).asLong();
                String gradeTypeString = decodedJWT.getClaim(GRADE_TYPE).asString();
                GradeType gradeType = GradeType.valueOf(gradeTypeString);
                Long id = decodedJWT.getClaim(APIKEY_ID).asLong();
                apiKey = new ApiKey(id, gradeType, memberId);
            } catch (Exception e) {
                throw new InvalidApiKeyException();
            }
            return Mono.just(apiKey);
        });
    }

    protected DecodedJWT decode(String token) throws InvalidApiKeyException {
        String secret = jwtConfigurationProperty.getSecret();
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    // reusable verifier instance
                    .build();

            return verifier.verify(token);
        } catch (JWTVerificationException exception){
            // Invalid signature/claims
            throw new InvalidApiKeyException();
        }
    }
}
