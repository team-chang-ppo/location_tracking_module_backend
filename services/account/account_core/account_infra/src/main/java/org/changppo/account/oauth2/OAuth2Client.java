package org.changppo.account.oauth2;

public abstract class OAuth2Client {
    public abstract void unlink(String identifier);
    protected abstract String getSupportedProvider();
    public final boolean supports(String provider) {
        return getSupportedProvider().equalsIgnoreCase(provider);
    }
}
