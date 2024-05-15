package org.changppo.account.security.oauth2.response;

import java.util.Map;

import static org.changppo.account.type.OAuth2Type.OAUTH2_GITHUB;

public class GitHubResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public GitHubResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return OAUTH2_GITHUB.name();
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }

    @Override
    public String getProfileImage() {
        return attribute.get("avatar_url").toString();
    }
}
