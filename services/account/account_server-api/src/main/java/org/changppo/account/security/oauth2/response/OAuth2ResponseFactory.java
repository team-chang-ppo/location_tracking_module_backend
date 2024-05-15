package org.changppo.account.security.oauth2.response;

import org.changppo.account.response.exception.oauth2.Oauth2LoginFailureException;

import java.util.Map;

public class OAuth2ResponseFactory {
    public static OAuth2Response getOAuth2Response(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "kakao" -> new KakaoResponse(attributes);
            case "github" -> new GitHubResponse(attributes);
            default -> throw new Oauth2LoginFailureException("Unsupported provider: " + registrationId);
        };
    }
}
