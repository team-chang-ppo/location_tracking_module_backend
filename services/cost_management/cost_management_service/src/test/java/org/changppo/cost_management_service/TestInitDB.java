package org.changppo.cost_management_service;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.cost_management_service.entity.apikey.ApiKey;
import org.changppo.cost_management_service.entity.apikey.Grade;
import org.changppo.cost_management_service.entity.apikey.GradeType;
import org.changppo.cost_management_service.entity.card.Card;
import org.changppo.cost_management_service.entity.card.PaymentGateway;
import org.changppo.cost_management_service.entity.card.PaymentGatewayType;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.entity.member.Role;
import org.changppo.cost_management_service.entity.member.RoleType;
import org.changppo.cost_management_service.repository.apikey.ApiKeyRepository;
import org.changppo.cost_management_service.repository.apikey.GradeRepository;
import org.changppo.cost_management_service.repository.card.CardRepository;
import org.changppo.cost_management_service.repository.card.PaymentGatewayRepository;
import org.changppo.cost_management_service.repository.member.MemberRepository;
import org.changppo.cost_management_service.repository.member.RoleRepository;
import org.changppo.cost_management_service.response.exception.apikey.GradeNotFoundException;
import org.changppo.cost_management_service.response.exception.card.PaymentGatewayNotFoundException;
import org.changppo.cost_management_service.response.exception.member.MemberNotFoundException;
import org.changppo.cost_management_service.response.exception.member.RoleNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
    private final PaymentGatewayRepository paymentGatewayRepository;
    private final CardRepository cardRepository;

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
    @Getter
    private final String testCardKey = "test-card-key";

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

    @Transactional
    public void initCard() {
        initPaymentGateway();
        initTestCard();
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
        bannedMember.banForPaymentFailure(LocalDateTime.now());

        memberRepository.saveAll(List.of(freeMember, normalMember, bannedMember));
    }

    private void initGrade() {
        gradeRepository.saveAll(
                Stream.of(GradeType.values()).map(Grade::new).collect(Collectors.toList())
        );
    }

    private void initTestApiKey() {
        Member freeMember = memberRepository.findByName(freeMemberName).orElseThrow(MemberNotFoundException::new);
        Member normalMember = memberRepository.findByName(normalMemberName).orElseThrow(MemberNotFoundException::new);
        Member bannedMember = memberRepository.findByName(bannedMemberName).orElseThrow(MemberNotFoundException::new);
        Grade freeGrade = gradeRepository.findByGradeType(GradeType.GRADE_FREE).orElseThrow(GradeNotFoundException::new);
        Grade classicGrade = gradeRepository.findByGradeType(GradeType.GRADE_CLASSIC).orElseThrow(GradeNotFoundException::new);

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
        bannedApiKey.banForPaymentFailure(LocalDateTime.now());
        apiKeyRepository.saveAll(List.of(freeApiKey, classicApiKey, classicApiKeyByBannedMember, bannedApiKey));
    }

    private void initPaymentGateway() {
        paymentGatewayRepository.saveAll(
                Stream.of(PaymentGatewayType.values()).map(PaymentGateway::new).collect(Collectors.toList())
        );
    }

    private void initTestCard() {
        Member normalMember = memberRepository.findByName(normalMemberName).orElseThrow(MemberNotFoundException::new);
        PaymentGateway kakaopayPaymentGateway = paymentGatewayRepository.findByPaymentGatewayType(PaymentGatewayType.PG_KAKAOPAY).orElseThrow(PaymentGatewayNotFoundException::new);

        Card card = Card.builder()
                .key(testCardKey)
                .member(normalMember)
                .paymentGateway(kakaopayPaymentGateway)
                .type("신용")
                .issuerCorporation("Test Bank")
                .acquirerCorporation("Test Acquirer")
                .bin("123456")
                .build();

        cardRepository.save(card);
    }
}