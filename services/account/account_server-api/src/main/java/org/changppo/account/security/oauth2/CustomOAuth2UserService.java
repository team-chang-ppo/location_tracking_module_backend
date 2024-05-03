package org.changppo.account.security.oauth2;

import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;
import org.changppo.account.response.exception.oauth2.MemberDeletionRequestedException;
import org.changppo.account.type.RoleType;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.repository.member.RoleRepository;
import org.changppo.account.response.exception.member.RoleNotFoundException;
import org.changppo.account.response.exception.oauth2.Oauth2LoginFailureException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.changppo.account.oauth2.kakao.KakaoConstants.KAKAO_REGISTRATION_ID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private static final List<String> SUPPORTED_REGISTRATION_IDS = List.of(KAKAO_REGISTRATION_ID);

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (!SUPPORTED_REGISTRATION_IDS.contains(registrationId)) {
            throw new Oauth2LoginFailureException("Login with OAuth2 provider failed: Unsupported provider - " + registrationId);
        }
        OAuth2Response oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        String name = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();

        Member member = memberRepository.findByNameWithRoles(name)
                .map(existingMember -> {
                    if (existingMember.isDeletionRequested()) {
                        throw new MemberDeletionRequestedException("Member deletion requested");
                    }
                    existingMember.updateInfo(oAuth2Response.getName(), oAuth2Response.getProfileImage());
                    return existingMember;
                })
                .orElseGet(() -> {
                    Role freeRole = roleRepository.findByRoleType(RoleType.ROLE_FREE).orElseThrow(RoleNotFoundException::new);
                    return memberRepository.save(
                        Member.builder()
                                .name(name)
                                .username(oAuth2Response.getName())
                                .profileImage(oAuth2Response.getProfileImage())
                                .roles(Set.of(freeRole))
                                .build());
                });

        return new CustomOAuth2User(member.getId(), name,  member.getMemberRoles().stream()
                                                            .map(memberRole -> new SimpleGrantedAuthority(memberRole.getRole().getRoleType().name()))
                                                            .collect(Collectors.toSet()));
    }
}
