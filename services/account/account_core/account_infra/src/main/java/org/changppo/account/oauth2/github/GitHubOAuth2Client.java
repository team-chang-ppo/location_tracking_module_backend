package org.changppo.account.oauth2.github;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.account.oauth2.OAuth2Client;
import org.changppo.account.oauth2.OAuth2Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.changppo.account.type.OAuth2Type.OAUTH2_GITHUB;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableConfigurationProperties(OAuth2Properties.class)
public class GitHubOAuth2Client extends OAuth2Client {

    private final RestTemplate restTemplate;
    private final OAuth2Properties oauth2Properties;

    @Override
    protected String getSupportedProvider() {
        return OAUTH2_GITHUB.name();
    }

    @Override
    public void unlink(String identifier) {
        try {
            HttpEntity<String> request = createUnlinkRequest(identifier);
            ResponseEntity<String> response = restTemplate.exchange(
                    GitHubConstants.GITHUB_URL + oauth2Properties.getGithub().getClientId() + GitHubConstants.GITHUB_UNLINK_URL,
                    HttpMethod.DELETE, request, String.class
            );
        handleResponse(response);
        } catch (Exception e) {
            log.error("Failed to unlink GitHub account.", e);
        }
    }

    private HttpEntity<String> createUnlinkRequest(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(oauth2Properties.getGithub().getClientId(), oauth2Properties.getGithub().getClientSecret());
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = String.format("{\"access_token\":\"%s\"}", accessToken);
        return new HttpEntity<>(body, headers);
    }

    private void handleResponse(ResponseEntity<?> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to process GitHub request.");
        }
    }
}
