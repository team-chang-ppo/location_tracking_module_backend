package org.changppo.account.service.domain.member;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.changppo.account.security.sign.CustomOAuth2UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

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
}
