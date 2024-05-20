package org.changppo.monitoring.security;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.commons.FailedResponseBody;
import org.changppo.commons.ResponseBody;
import org.changppo.commons.SuccessResponseBody;
import org.changppo.monioring.domain.error.RemoteSessionFetchFailedException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RestRemoteSessionRetrieveStrategy implements RemoteSessionRetrieveStrategy{
    private final static ParameterizedTypeReference<ResponseBody<SessionResponsePayload>> SESSION_API_RESPONSE_TYPE = new ParameterizedTypeReference<>() {};
    private final SessionQueryProperties sessionQueryProperties;
    private final RestTemplate restTemplate;
    @Nullable
    @Override
    public RemoteSessionAuthentication retrieve(String sessionId) {
        String sessionQueryEndpoint = sessionQueryProperties.getSessionQueryEndpoint();
        // 쿠키값 설정

        // 쿠키값을 헤더에 설정
        StringBuilder cookieBuilder = new StringBuilder();
        cookieBuilder.append(sessionQueryProperties.getSessionCookieName()).append("=").append(sessionId);
        RequestEntity<Void> requestEntity = RequestEntity.get(sessionQueryEndpoint)
                .header(HttpHeaders.COOKIE, cookieBuilder.toString())
                .build();

        // 요청
        final ResponseEntity<ResponseBody<SessionResponsePayload>> response;
        try {
            response = restTemplate.exchange(requestEntity, SESSION_API_RESPONSE_TYPE);
        } catch (RestClientException e) {
            throw new RemoteSessionFetchFailedException();
        }
        HttpStatusCode statusCode = response.getStatusCode();
        ResponseBody<SessionResponsePayload> payload = response.getBody();
        // 500
        if (statusCode.is5xxServerError() || payload == null) {
            throw new RemoteSessionFetchFailedException();
        }
        // 4xx
        if (statusCode.is4xxClientError() || payload instanceof FailedResponseBody<?>) {
            log.debug("Failed to fetch session info. status: {}, payload: {}", statusCode, payload);
            return null;
        }
        if (!(payload instanceof SuccessResponseBody<SessionResponsePayload> successResponseBody)) {
            throw new IllegalStateException("Unexpected response body type: " + payload.getClass());
        }

        // 성공
        var data = successResponseBody.getResult().data();
        Long memberId = data.memberId();
        List<String> roles = data.roles();
        if (memberId == null || roles == null) {
            log.error("Session Api Server returns incomplete data. memberId: {}, roles: {}", memberId, roles);
            throw new RemoteSessionFetchFailedException();
        }

        return new RemoteSessionAuthentication(memberId, roles);
    }

    public record SessionResponsePayload(
            SessionPayload data
    ) {

        public record SessionPayload(
                Long memberId,
                List<String> roles
        ){
        }
    }
}
