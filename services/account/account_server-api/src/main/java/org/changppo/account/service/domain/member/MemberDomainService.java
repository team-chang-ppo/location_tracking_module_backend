package org.changppo.account.service.domain.member;

import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.response.exception.member.MemberNotFoundException;
import org.changppo.account.service.dto.member.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
public class MemberDomainService {

    private final MemberRepository memberRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public Member createMember(String name, String username, String profileImage, Role role) {
        Member member = Member.builder()
                .name(name)
                .username(username)
                .profileImage(profileImage)
                .role(role)
                .build();
        return memberRepository.save(member);
    }

    public Member getMember(Long id) {
        return memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
    }

    public Member getMemberWithRoles(Long memberId) {
        return memberRepository.findByIdWithRoles(memberId).orElseThrow(MemberNotFoundException::new);
    }

    public Optional<Member> getOptionalMemberByNameWithRoles(String name) {
        return memberRepository.findByNameWithRoles(name);
    }

    public MemberDto getMemberDto(Long id) {
        return memberRepository.findDtoById(id).orElseThrow(MemberNotFoundException::new);
    }

    public Page<MemberDto> getMemberDtoPage(Pageable pageable) {
        return memberRepository.findAllDtos(pageable);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void banMemberPaymentFailure(Member member) {
        member.banForPaymentFailure(LocalDateTime.now());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void unbanMemberPaymentFailure(Member member) {
        member.unbanForPaymentFailure();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void requestMemberDeletion(Member member) {
        member.requestDeletion(LocalDateTime.now());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void cancelMemberDeletionRequest(Member member) {
        member.cancelDeletionRequest();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void banMemberByAdmin(Member member) {
        member.banByAdmin(LocalDateTime.now());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void unbanMemberByAdmin(Member member) {
        member.unbanByAdmin();
    }
}
