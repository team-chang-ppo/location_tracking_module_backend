package org.changppo.tracking.jwt;

import org.changppo.tracking.domain.TrackingContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public record JwtAuthentication(
        String trackingId,
        String apiKeyId,
        List<String> scopes
) implements Authentication {

    public JwtAuthentication(TrackingContext principal) {
        this(principal.trackingId(), principal.apiKeyId(), principal.scopes());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return scopes.stream()
                .map(scope -> (GrantedAuthority) () -> scope)
                .toList();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return trackingId;
    }

    @Override
    public Object getPrincipal() {
        return new TrackingContext(trackingId, apiKeyId, scopes);
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return trackingId;
    }
}