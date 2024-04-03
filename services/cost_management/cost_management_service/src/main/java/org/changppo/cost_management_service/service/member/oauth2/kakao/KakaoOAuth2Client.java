package org.changppo.cost_management_service.service.member.oauth2.kakao;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.response.exception.oauth2.kakao.KakaoOAuth2UnlinkFailureException;
import org.changppo.cost_management_service.service.member.oauth2.OAuth2Client;
import org.changppo.cost_management_service.service.member.oauth2.OAuth2Properties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class KakaoOAuth2Client extends OAuth2Client {

    private final RestTemplate restTemplate;
    private final OAuth2Properties oauth2Properties;
    private static final String KAKAO_REGISTRATION_ID = "kakao";
    private static final String KAKAO_UNLINK_URL = "https://kapi.kakao.com/v1/user/unlink";
    private static final String KAKAO_UNLINK_REQUEST_BODY_FORMAT = "target_id_type=user_id&target_id=%s";

    @Override
    protected String getSupportedProvider() {
        return KAKAO_REGISTRATION_ID;
    }

    @Override
    public void unlink(String MemberId) {
        try{
        HttpEntity<String> request = createRequest(MemberId);
        ResponseEntity<String> response = restTemplate.exchange(KAKAO_UNLINK_URL, HttpMethod.POST, request, String.class);
        handleResponse(response);
        } catch (Exception e) {
            throw new KakaoOAuth2UnlinkFailureException(e);
        }
    }

    private HttpEntity<String> createRequest(String providerMemberId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, oauth2Properties.getKakao().getAdminKey());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String body = String.format(KAKAO_UNLINK_REQUEST_BODY_FORMAT, providerMemberId);
        return new HttpEntity<>(body, headers);
    }

    private void handleResponse(ResponseEntity<String> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to unlink Kakao account.");
        }
    }
}