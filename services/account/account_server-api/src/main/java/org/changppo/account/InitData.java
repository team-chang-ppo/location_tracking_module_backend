package org.changppo.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.account.entity.apikey.Grade;
import org.changppo.account.entity.card.PaymentGateway;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;
import org.changppo.account.repository.apikey.GradeRepository;
import org.changppo.account.repository.card.PaymentGatewayRepository;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.repository.member.RoleRepository;
import org.changppo.account.response.exception.member.RoleNotFoundException;
import org.changppo.account.type.GradeType;
import org.changppo.account.type.PaymentGatewayType;
import org.changppo.account.type.RoleType;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Profile("local")
@Slf4j
public class InitData {

    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final GradeRepository gradeRepository;
    private final PaymentGatewayRepository paymentGatewayRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initData() {
        initRole();
        // initMember();
        initGrade();
        initPaymentGateway();
    }

    private void initRole() {
        roleRepository.saveAll(
                Stream.of(RoleType.values()).map(Role::new).collect(Collectors.toList())
        );
    }

    private void initMember() {
        Role freeRole = roleRepository.findByRoleType(RoleType.ROLE_FREE).orElseThrow(RoleNotFoundException::new);
        Role normalRole = roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new);

        Member freeMember = Member.builder()
                .name("freeMemberName")
                .username("free")
                .profileImage("freeMemberProfileImage")
                .roles(Collections.singleton(freeRole))
                .build();
        Member normalMember = Member.builder()
                .name("normalMemberName")
                .username("normal")
                .profileImage("normalMemberProfileImage")
                .roles(Collections.singleton(normalRole))
                .build();
        Member bannedMember = Member.builder()
                .name("bannedMemberName")
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

    private void initPaymentGateway() {
        paymentGatewayRepository.saveAll(
                Stream.of(PaymentGatewayType.values()).map(PaymentGateway::new).collect(Collectors.toList())
        );
    }
}
