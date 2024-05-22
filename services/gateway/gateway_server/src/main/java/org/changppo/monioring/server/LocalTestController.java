package org.changppo.monioring.server;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import lombok.RequiredArgsConstructor;
import org.changppo.monioring.server.apikey.ApiKey;
import org.changppo.monioring.server.apikey.GradeType;
import org.changppo.monioring.server.apikey.JwtConfigurationProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LocalTestController {

    private final JwtConfigurationProperty jwtConfigurationProperty;

    @GetMapping("/test")
    public String test() {
        //create jwt token
        ApiKey apiKey = new ApiKey(1L, GradeType.GRADE_CLASSIC, 1L);
        String secret = jwtConfigurationProperty.getSecret();
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withClaim("MEMBER_ID", apiKey.memberId())
                    .withClaim("GRADE_TYPE", apiKey.gradeType().name())
                    .withClaim("ID", apiKey.apiKeyId())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception){
            // Invalid Signing configuration / Couldn't convert Claims.
            return "error";
        }
    }
}
