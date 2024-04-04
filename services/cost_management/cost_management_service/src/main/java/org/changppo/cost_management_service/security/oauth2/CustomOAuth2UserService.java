package org.changppo.cost_management_service.security.oauth2;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.entity.member.Role;
import org.changppo.cost_management_service.entity.member.RoleType;
import org.changppo.cost_management_service.response.exception.member.RoleNotFoundException;
import org.changppo.cost_management_service.response.exception.oauth2.Oauth2LoginFailureException;
import org.changppo.cost_management_service.repository.member.MemberRepository;
import org.changppo.cost_management_service.repository.member.RoleRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.changppo.cost_management_service.service.member.oauth2.kakao.KakaoConstants.KAKAO_REGISTRATION_ID;

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

        Member member = memberRepository.findByNameIgnoringDeleted(name)
                .map(existingMember -> {
                    if (existingMember.isDeleted()) {
                        Role freeRole = roleRepository.findByRoleType(RoleType.ROLE_FREE).orElseThrow(RoleNotFoundException::new);
                        existingMember.reactivate(oAuth2Response.getName(), oAuth2Response.getProfileImage(), Set.of(freeRole));
                    } else {
                        existingMember.updateInfo(oAuth2Response.getName(), oAuth2Response.getProfileImage());
                    }
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
