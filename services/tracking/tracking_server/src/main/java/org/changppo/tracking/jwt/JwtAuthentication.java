package org.changppo.tracking.jwt;

import org.changppo.tracking.domain.TrackingContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public record JwtAuthentication(
        String trackingId,
        String authority
) implements Authentication {

    public JwtAuthentication(TrackingContext principal) {
        this(principal.trackingId(), principal.authority());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        GrantedAuthority authority = new SimpleGrantedAuthority(this.authority);
        return Collections.singleton(authority);
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
        return new TrackingContext(trackingId, authority);
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
