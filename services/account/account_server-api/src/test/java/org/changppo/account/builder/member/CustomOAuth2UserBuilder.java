package org.changppo.account.builder.member;

import org.changppo.account.entity.member.Member;
import org.changppo.account.security.sign.CustomOAuth2UserDetails;
import org.changppo.account.type.RoleType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

public class CustomOAuth2UserBuilder {

    public static CustomOAuth2UserDetails buildCustomOAuth2User(Member member) {
        return new CustomOAuth2UserDetails(member.getId(), member.getName(), member.getPassword(), Collections.singleton(new SimpleGrantedAuthority(member.getRole().getRoleType().name())));
    }

    public static CustomOAuth2UserDetails buildCustomOAuth2User(Long id, String name, RoleType role) {
        return new CustomOAuth2UserDetails(id, name, null, Collections.singleton(new SimpleGrantedAuthority(role.name())));
    }
}
