package org.changppo.cost_management_service.security.oauth;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.entity.member.Role;
import org.changppo.cost_management_service.entity.member.RoleType;
import org.changppo.cost_management_service.exception.LoginFailureException;
import org.changppo.cost_management_service.exception.RoleNotFoundException;
import org.changppo.cost_management_service.repository.member.MemberRepository;
import org.changppo.cost_management_service.repository.member.RoleRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        if (!"kakao".equals(userRequest.getClientRegistration().getRegistrationId())) {
            throw new LoginFailureException();
        }

        OAuth2Response oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        String name = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();

        Member member = memberRepository.findByName(name)
                .map(existingMember -> {
                    if (existingMember.isDeleted()) {
                        existingMember.reactivate(oAuth2Response.getName(), oAuth2Response.getProfileImage());
                    } else {
                        existingMember.updateInfo(oAuth2Response.getName(), oAuth2Response.getProfileImage());
                    }
                    return existingMember;
                })
                .orElseGet(() -> {
                    Role role = roleRepository.findByRoleType(RoleType.ROLE_FREE)
                            .orElseThrow(RoleNotFoundException::new);

                    return memberRepository.save(
                        Member.builder()
                                .name(name)
                                .username(oAuth2Response.getName())
                                .profileImage(oAuth2Response.getProfileImage())
                                .roles(Set.of(role))
                                .build());
                });


        return new CustomOAuth2User(member.getId(), name,  member.getRoles().stream()
                                            .map(memberRole -> new SimpleGrantedAuthority(memberRole.getRole().getRoleType().name()))
                                            .collect(Collectors.toSet()));
    }
}
