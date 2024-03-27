package org.changppo.cost_management_service;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.entity.member.Role;
import org.changppo.cost_management_service.entity.member.RoleType;
import org.changppo.cost_management_service.exception.RoleNotFoundException;
import org.changppo.cost_management_service.repository.member.MemberRepository;
import org.changppo.cost_management_service.repository.member.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestInitDB {

    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;

    @Getter
    private final String adminName = "kakao_0000";
    @Getter
    private final String member1Name = "kakao_1234";
    @Getter
    private final String member2Name = "kakao_2345";
    @Getter
    private final String member3Name = "kakao_3456";

    @Transactional
    public void initDB() {
        initRole();
        initTestAdmin();
        initTestMember();
    }

    private void initRole() {
        roleRepository.saveAll(
                List.of(RoleType.values()).stream().map(roleType -> new Role(roleType)).collect(Collectors.toList())
        );
    }

    private void initTestAdmin() {
        Role adminRole = roleRepository.findByRoleType(RoleType.ROLE_ADMIN).orElseThrow(RoleNotFoundException::new);
        Role normalRole = roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new);
        Member adminMember = Member.builder()
                .name(adminName)
                .username("admin")
                .profileImage("adminProfileImage")
                .roles(new HashSet<>(Arrays.asList(adminRole, normalRole)))
                .build();
        memberRepository.save(adminMember);
    }

    private void initTestMember() {
        Role normalRole = roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new);
        memberRepository.saveAll(
                List.of(
                        Member.builder()
                                .name(member1Name)
                                .username("member1")
                                .profileImage("profileImage1")
                                .roles(Collections.singleton(normalRole))
                                .build(),
                        Member.builder()
                                .name(member2Name)
                                .username("member2")
                                .profileImage("profileImage2")
                                .roles(Collections.singleton(normalRole))
                                .build(),
                        Member.builder()
                                .name(member3Name)
                                .username("member3")
                                .profileImage("profileImage3")
                                .roles(Collections.singleton(normalRole))
                                .build()
                )
        );
    }
}