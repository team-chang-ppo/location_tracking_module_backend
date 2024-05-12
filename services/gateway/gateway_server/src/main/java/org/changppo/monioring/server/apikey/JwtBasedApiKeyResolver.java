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
    private final static String MEMBER_ID_CLAIM = "MEMBER_ID";
    private final static String GRADE_TYPE_CLAIM = "GRADE_TYPE";
    private final static String ID_CLAIM = "ID";
    private final JwtConfigurationProperty jwtConfigurationProperty;

    @Override
    public Mono<ApiKey> resolve(ServerWebExchange exchange) {

        String token = exchange.getRequest().getHeaders().getFirst(GatewayConstant.API_KEY_HEADER);
        if (token == null) {
            return Mono.empty();
        }

        DecodedJWT decodedJWT = decode(token);
        ApiKey apiKey;
        try {
            Long memberId = decodedJWT.getClaim(MEMBER_ID_CLAIM).asLong();
            String gradeTypeString = decodedJWT.getClaim(GRADE_TYPE_CLAIM).asString();
            GradeType gradeType = GradeType.valueOf(gradeTypeString);
            Long id = decodedJWT.getClaim(ID_CLAIM).asLong();
            apiKey = new ApiKey(id, gradeType, memberId);
        } catch (Exception e) {
            throw new InvalidApiKeyException();
        }
        // TODO redis bitmap Check
        return Mono.just(apiKey);
    }

    protected DecodedJWT decode(String token) throws InvalidApiKeyException {
        String secret = jwtConfigurationProperty.getSecret();
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    // reusable verifier instance
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT;
        } catch (JWTVerificationException exception){
            // Invalid signature/claims
            throw new InvalidApiKeyException();
        }
    }
}
