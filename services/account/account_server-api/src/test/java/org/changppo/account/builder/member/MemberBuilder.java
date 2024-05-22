package org.changppo.account.builder.member;

import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;

public class MemberBuilder {
    public static Member buildMember(Role role) {
        return Member.builder()
                .name("testName")
                .username("testUsername")
                .profileImage("testProfileImage")
                .role(role)
                .build();
    }

    public static Member buildMember(String name, String username, String profileImage, Role role) {
        return Member.builder()
                .name(name)
                .username(username)
                .profileImage(profileImage)
                .role(role)
                .build();
    }
}
