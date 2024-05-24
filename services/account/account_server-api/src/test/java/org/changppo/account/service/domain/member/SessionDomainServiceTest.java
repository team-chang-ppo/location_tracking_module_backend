package org.changppo.account.service.domain.member;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.changppo.account.builder.member.CustomOAuth2UserBuilder;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;
import org.changppo.account.response.exception.member.UpdateAuthenticationFailureException;
import org.changppo.account.security.sign.CustomOAuth2UserDetails;
import org.changppo.account.type.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.changppo.account.builder.member.MemberBuilder.buildMember;
import static org.changppo.account.builder.member.RoleBuilder.buildRole;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SessionDomainServiceTest {

    @InjectMocks
    SessionDomainService sessionDomainService;
    @Mock
    SessionRegistry sessionRegistry;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    HttpSession session;
    @Mock
    SecurityContext securityContext;
    @Mock
    OAuth2AuthenticationToken oauth2AuthenticationToken;
    @Captor
    ArgumentCaptor<Cookie> cookieCaptor;

    Member member;
    Role role;

    @BeforeEach
    void setup() {
        role = buildRole(RoleType.ROLE_NORMAL);
        member = buildMember(role);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void invalidateSessionAndClearCookiesTest() {
        // given
        given(request.getSession(false)).willReturn(session);

        // when
        sessionDomainService.invalidateSessionAndClearCookies(request, response);

        // then
        verify(session).invalidate();
        verify(response).addCookie(cookieCaptor.capture());
        Cookie cookie = cookieCaptor.getValue();
        assertThat(cookie.getName()).isEqualTo("JSESSIONID");
        assertThat(cookie.getValue()).isNull();
        assertThat(cookie.getMaxAge()).isEqualTo(0);
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/");
    }

    @Test
    void expireSessionsTest() {
        // given
        CustomOAuth2UserDetails customOAuth2UserDetails = CustomOAuth2UserBuilder.buildCustomOAuth2User(member);
        given(sessionRegistry.getAllPrincipals()).willReturn(List.of(customOAuth2UserDetails));
        SessionInformation sessionInformation = mock(SessionInformation.class);
        given(sessionRegistry.getAllSessions(customOAuth2UserDetails, false)).willReturn(List.of(sessionInformation));

        // when
        sessionDomainService.expireSessions(member.getName());

        // then
        verify(sessionInformation).expireNow();
    }

    @Test
    void updateAuthenticationTest() {
        // given
        given(securityContext.getAuthentication()).willReturn(oauth2AuthenticationToken);
        given(oauth2AuthenticationToken.getAuthorizedClientRegistrationId()).willReturn("clientRegistrationId");

        // when
        sessionDomainService.updateAuthentication(member);

        // then
        verify(securityContext).setAuthentication(any(OAuth2AuthenticationToken.class));
    }

    @Test
    void updateAuthenticationFailureTest() {
        // given
        given(securityContext.getAuthentication()).willReturn(null);

        // when, then
        assertThatThrownBy(() -> sessionDomainService.updateAuthentication(member))
                .isInstanceOf(UpdateAuthenticationFailureException.class);
    }
}
