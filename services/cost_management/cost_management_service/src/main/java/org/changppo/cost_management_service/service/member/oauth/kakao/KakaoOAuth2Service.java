package org.changppo.cost_management_service.service.member.oauth.kakao;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.service.member.oauth.OAuth2Service;
import org.changppo.cost_management_service.service.member.oauth.OAuthProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class KakaoOAuth2Service implements OAuth2Service {

    private final RestTemplate restTemplate;
    private final OAuthProperties oauthProperties;
    private static final String KAKAO_UNLINK_URL = "https://kapi.kakao.com/v1/user/unlink";
    private static final String KAKAO_UNLINK_REQUEST_BODY_FORMAT = "target_id_type=user_id&target_id=%s";
    @Override
    public void unlinkMember(String providerMemberId) {
        HttpEntity<String> request = createRequest(providerMemberId);
        ResponseEntity<String> response = restTemplate.exchange(KAKAO_UNLINK_URL, HttpMethod.POST, request, String.class);
        handleResponse(response);
    }

    private HttpEntity<String> createRequest(String providerMemberId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, oauthProperties.getKakao().getAdminKey());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String body = String.format(KAKAO_UNLINK_REQUEST_BODY_FORMAT, providerMemberId);
        return new HttpEntity<>(body, headers);
    }

    private void handleResponse(ResponseEntity<String> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to unlink Kakao member: " + response.getBody());
        }
    }

    @Override
    public boolean supports(String provider) {
        return "kakao".equalsIgnoreCase(provider);
    }
}