package org.changppo.account.service.domain.member;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.member.Member;
import org.changppo.account.response.exception.member.UpdateAuthenticationFailureException;
import org.changppo.account.security.sign.CustomOAuth2UserDetails;
import org.changppo.account.type.RoleType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class SessionDomainService {

    private final SessionRegistry sessionRegistry;

    public void invalidateSessionAndClearCookies(HttpServletRequest request, HttpServletResponse response) {
        clearSecurityContext();
        invalidateHttpSession(request);
        clearCookies(response);
    }

    private void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void invalidateHttpSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    private void clearCookies(HttpServletResponse response) {
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        // refreshCookie.setSecure(true);
        response.addCookie(cookie);
    }

    public void expireSessions(String username) {
        sessionRegistry.getAllPrincipals().stream()
                .filter(principal -> principal instanceof CustomOAuth2UserDetails)
                .map(principal -> (CustomOAuth2UserDetails) principal)
                .filter(userDetails -> userDetails.getName().equals(username))
                .flatMap(userDetails -> sessionRegistry.getAllSessions(userDetails, false).stream())
                .forEach(SessionInformation::expireNow);
    }

    public void updateAuthentication(Member member) {
        Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(this::isOAuth2AuthenticationToken)
                .map(OAuth2AuthenticationToken.class::cast)
                .map(oauth2AuthenticationToken -> getOAuth2AuthenticationToken(oauth2AuthenticationToken, member))
                .ifPresentOrElse(
                        SecurityContextHolder.getContext()::setAuthentication,
                        () -> {
                            throw new UpdateAuthenticationFailureException();
                        }
                );
    }

    private boolean isOAuth2AuthenticationToken(Authentication authentication) {
        return authentication instanceof OAuth2AuthenticationToken;
    }

    private OAuth2AuthenticationToken getOAuth2AuthenticationToken(OAuth2AuthenticationToken oauth2AuthenticationToken, Member member) {
        return new OAuth2AuthenticationToken(
                getPrincipal(member),
                getAuthority(member),
                oauth2AuthenticationToken.getAuthorizedClientRegistrationId()
        );
    }

    private CustomOAuth2UserDetails getPrincipal(Member member) {
        return new CustomOAuth2UserDetails(
                member.getId(),
                member.getName(),
                member.getPassword(),
                getAuthority(member)
        );
    }

    private Set<GrantedAuthority> getAuthority(Member member) {
        RoleType roleType = member.getRole().getRoleType();
        return Collections.singleton(new SimpleGrantedAuthority(roleType.name()));
    }
}
