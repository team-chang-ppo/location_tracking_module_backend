package org.changppo.cost_management_service.builder.member;

import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.entity.member.RoleType;
import org.changppo.cost_management_service.security.oauth.CustomOAuth2User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public class CustomOAuth2UserBuilder {

    public static CustomOAuth2User buildCustomOAuth2User(Member member) {
        return new CustomOAuth2User(member.getId(), member.getName(), member.getRoles().stream()
                                                                        .map(memberRole -> new SimpleGrantedAuthority(memberRole.getRole().getRoleType().name()))
                                                                        .collect(Collectors.toSet()));
    }

    public static CustomOAuth2User buildCustomOAuth2User(Long id, String name, Set<RoleType> roles) {
        return new CustomOAuth2User(id, name, roles.stream()
                                            .map(roleType -> new SimpleGrantedAuthority(roleType.name()))
                                            .collect(Collectors.toSet()));
    }
}