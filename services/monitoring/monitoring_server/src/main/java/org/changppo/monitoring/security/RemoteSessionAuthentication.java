package org.changppo.monitoring.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RemoteSessionAuthentication implements Authentication {
    private final Long memberId;
    private final List<GrantedAuthority> authorities;

    public RemoteSessionAuthentication(Long memberId, List<String> authorities) {
        this.memberId = memberId;
        this.authorities = Collections.unmodifiableList(authorities.stream().map(a -> (GrantedAuthority) () -> a).toList());
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return this.memberId;
    }

    @Override
    public Object getPrincipal() {
        return this.memberId;
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
        return this.memberId.toString();
    }
}
