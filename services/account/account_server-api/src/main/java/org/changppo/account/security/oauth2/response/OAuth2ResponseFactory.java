package org.changppo.account.security.oauth2.response;

import org.changppo.account.response.exception.oauth2.Oauth2LoginFailureException;

import java.util.Map;

import static org.changppo.account.oauth2.github.GitHubConstants.GITHUB_REGISTRATION_ID;
import static org.changppo.account.oauth2.kakao.KakaoConstants.KAKAO_REGISTRATION_ID;

public class OAuth2ResponseFactory {
    public static OAuth2Response getOAuth2Response(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case KAKAO_REGISTRATION_ID -> new KakaoResponse(attributes);
            case GITHUB_REGISTRATION_ID -> new GitHubResponse(attributes);
            default -> throw new Oauth2LoginFailureException("Unsupported provider: " + registrationId);
        };
    }
}
