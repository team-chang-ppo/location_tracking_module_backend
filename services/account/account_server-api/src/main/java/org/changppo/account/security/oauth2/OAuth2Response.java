package org.changppo.account.security.oauth2;

public interface OAuth2Response {
    String getProvider();
    String getProviderId();
    String getName();
    String getProfileImage();
}
