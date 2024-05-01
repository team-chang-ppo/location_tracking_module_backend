package org.changppo.account.oauth2;

import org.changppo.account.response.ClientResponse;

public abstract class OAuth2Client {
    public abstract void unlink(String memberId);
    protected abstract String getSupportedProvider();
    public final boolean supports(String provider) {
        return getSupportedProvider().equalsIgnoreCase(provider);
    }
}
