package org.changppo.cost_management_service.security;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.changppo.cost_management_service.entity.member.RoleType;
import org.changppo.cost_management_service.security.oauth.CustomOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PrincipalHandler {

    public static Long extractId() {
        return getUserDetails().getId();
    }
    public static String extractName() {
        return getUserDetails().getName();
    }

    public static Set<RoleType> extractMemberRoles() {
        return getUserDetails().getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .map(strAuth -> RoleType.valueOf(strAuth))
                .collect(Collectors.toSet());
    }

    private static CustomOAuth2User getUserDetails() {
        return (CustomOAuth2User) getAuthentication().getPrincipal();
    }

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}