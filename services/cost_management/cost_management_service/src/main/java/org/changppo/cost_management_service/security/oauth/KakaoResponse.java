package org.changppo.cost_management_service.security.oauth;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attribute;
    private final Map<String, Object> profileAttributes;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
        this.profileAttributes = extractProfileAttributes(attribute);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> extractProfileAttributes(Map<String, Object> attribute) {
        if (attribute.get("kakao_account") instanceof Map<?, ?>) {
            Map<String, Object> kakaoAccountAttributes = (Map<String, Object>) attribute.get("kakao_account");
            if (kakaoAccountAttributes.get("profile") instanceof Map<?, ?>) {
                return (Map<String, Object>) kakaoAccountAttributes.get("profile");
            }
        }
        throw new IllegalArgumentException("Profile attributes could not be extracted.");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getName() {
        return profileAttributes.get("nickname").toString();
    }

    @Override
    public String getProfileImage() {
        return profileAttributes.get("profile_image_url").toString();
    }
}
