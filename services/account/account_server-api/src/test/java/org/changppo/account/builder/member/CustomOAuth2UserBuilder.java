package org.changppo.account.builder.member;

import org.changppo.account.entity.member.Member;
import org.changppo.account.type.RoleType;
import org.changppo.account.security.oauth2.CustomOAuth2UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public class CustomOAuth2UserBuilder {

    public static CustomOAuth2UserDetails buildCustomOAuth2User(Member member) {
        return new CustomOAuth2UserDetails(member.getId(), member.getName(), member.getPassword(), member.getMemberRoles().stream()
                                                                        .map(memberRole -> new SimpleGrantedAuthority(memberRole.getRole().getRoleType().name()))
                                                                        .collect(Collectors.toSet()));
    }

    public static CustomOAuth2UserDetails buildCustomOAuth2User(Long id, String name, Set<RoleType> roles) {
        return new CustomOAuth2UserDetails(id, name, null, roles.stream()
                                            .map(roleType -> new SimpleGrantedAuthority(roleType.name()))
                                            .collect(Collectors.toSet()));
    }
}
