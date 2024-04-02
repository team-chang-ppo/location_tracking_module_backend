package org.changppo.cost_management_service.security.oauth2;

public interface OAuth2Response {
    String getProvider();
    String getProviderId();
    String getName();
    String getProfileImage();
}
