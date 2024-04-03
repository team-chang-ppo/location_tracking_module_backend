package org.changppo.cost_management_service.service.member.oauth2;

public abstract class OAuth2Client {
    public abstract void unlink(String memberId);
    protected abstract String getSupportedProvider();
    public final boolean supports(String provider) {
        return getSupportedProvider().equalsIgnoreCase(provider);
    }
}