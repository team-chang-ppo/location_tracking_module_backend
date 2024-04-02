package org.changppo.cost_management_service;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.cost_management_service.entity.apikey.ApiKey;
import org.changppo.cost_management_service.entity.apikey.Grade;
import org.changppo.cost_management_service.entity.apikey.GradeType;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.entity.member.Role;
import org.changppo.cost_management_service.entity.member.RoleType;
import org.changppo.cost_management_service.exception.member.RoleNotFoundException;
import org.changppo.cost_management_service.repository.apikey.ApiKeyRepository;
import org.changppo.cost_management_service.repository.apikey.GradeRepository;
import org.changppo.cost_management_service.repository.member.MemberRepository;
import org.changppo.cost_management_service.repository.member.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestInitDB {

    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final GradeRepository gradeRepository;
    private final ApiKeyRepository apiKeyRepository;

    @Getter
    private final String adminMemberName = "kakao_0000";
    @Getter
    private final String freeMemberName = "kakao_1234";
    @Getter
    private final String normalMemberName = "kakao_2345";
    @Getter
    private final String bannedMemberName = "kakao_3456";
    @Getter
    private final String freeApiKeyValue = "free-api-key";
    @Getter
    private final String classicApiKeyValue = "classic-api-key";
    @Getter
    private final String classicApiKeyByBannedMemberValue = "classic-api-key-by-banned-member";
    @Getter
    private final String bannedApiKeyValue = "banned-api-key";

    @Transactional
    public void initMember() {
        initRole();
        initTestAdmin();
        initTestMember();
    }

    @Transactional
    public void initApiKey() {
        initGrade();
        initTestApiKey();
    }

    private void initRole() {
        roleRepository.saveAll(
                Stream.of(RoleType.values()).map(Role::new).collect(Collectors.toList())
        );
    }

    private void initTestAdmin() {
        Role adminRole = roleRepository.findByRoleType(RoleType.ROLE_ADMIN).orElseThrow(RoleNotFoundException::new);
        Role normalRole = roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new);
        Member adminMember = Member.builder()
                .name(adminMemberName)
                .username("admin")
                .profileImage("adminMemberProfileImage")
                .roles(new HashSet<>(Arrays.asList(adminRole, normalRole)))
                .build();
        memberRepository.save(adminMember);
    }

    private void initTestMember() {
        Role freeRole = roleRepository.findByRoleType(RoleType.ROLE_FREE).orElseThrow(RoleNotFoundException::new);
        Role normalRole = roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new);

        Member freeMember = Member.builder()
                .name(freeMemberName)
                .username("free")
                .profileImage("freeMemberProfileImage")
                .roles(Collections.singleton(freeRole))
                .build();
        Member normalMember = Member.builder()
                .name(normalMemberName)
                .username("normal")
                .profileImage("normalMemberProfileImage")
                .roles(Collections.singleton(normalRole))
                .build();
        Member bannedMember = Member.builder()
                .name(bannedMemberName)
                .username("banned")
                .profileImage("bannedMemberProfileImage")
                .roles(Collections.singleton(normalRole))
                .build();
        bannedMember.ban();

        memberRepository.saveAll(List.of(freeMember, normalMember, bannedMember));
    }

    private void initGrade() {
        gradeRepository.saveAll(
                Stream.of(GradeType.values()).map(Grade::new).collect(Collectors.toList())
        );
    }

    private void initTestApiKey() {
        Member freeMember = memberRepository.findByName(freeMemberName).orElseThrow();
        Member normalMember = memberRepository.findByName(normalMemberName).orElseThrow();
        Member bannedMember = memberRepository.findByName(bannedMemberName).orElseThrow();
        Grade freeGrade = gradeRepository.findByGradeType(GradeType.GRADE_FREE).orElseThrow();
        Grade classicGrade = gradeRepository.findByGradeType(GradeType.GRADE_CLASSIC).orElseThrow();

        ApiKey freeApiKey = ApiKey.builder()
                .value(freeApiKeyValue)
                .grade(freeGrade)
                .member(freeMember)
                .build();
        ApiKey classicApiKey = ApiKey.builder()
                .value(classicApiKeyValue)
                .grade(classicGrade)
                .member(normalMember)
                .build();
        ApiKey classicApiKeyByBannedMember = ApiKey.builder()
                .value(classicApiKeyByBannedMemberValue)
                .grade(classicGrade)
                .member(bannedMember)
                .build();
        ApiKey bannedApiKey = ApiKey.builder()
                .value(bannedApiKeyValue)
                .grade(freeGrade)
                .member(normalMember)
                .build();
        bannedApiKey.ban();
        apiKeyRepository.saveAll(List.of(freeApiKey, classicApiKey, classicApiKeyByBannedMember, bannedApiKey));
    }
}