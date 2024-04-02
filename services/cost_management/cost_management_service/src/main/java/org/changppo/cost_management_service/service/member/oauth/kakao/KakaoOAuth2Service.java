package org.changppo.cost_management_service.service.member.oauth.kakao;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.service.member.oauth.OAuth2Service;
import org.changppo.cost_management_service.service.member.oauth.OAuthProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoOAuth2Service implements OAuth2Service {

    private final RestTemplate restTemplate;
    private final OAuthProperties oauthProperties;

    @Override
    public void unlinkUser(String providerUserId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + oauthProperties.getKakao().getAdminKey());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "target_id_type=user_id&target_id=" + providerUserId;
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        String url = "https://kapi.kakao.com/v1/user/unlink";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to unlink Kakao user: " + response.getBody());
        }
    }

    @Override
    public boolean supports(String provider) {
        return "kakao".equalsIgnoreCase(provider);
    }
}