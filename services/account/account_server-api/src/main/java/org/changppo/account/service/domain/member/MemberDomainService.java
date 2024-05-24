package org.changppo.account.service.domain.member;

import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.member.Member;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.response.exception.member.MemberNotFoundException;
import org.changppo.account.service.dto.member.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
public class MemberDomainService {

    private final MemberRepository memberRepository;

    public Member getMember(Long id) {
        return memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
    }

    public Member getMemberWithRoles(Long memberId) {
        return memberRepository.findByIdWithRoles(memberId).orElseThrow(MemberNotFoundException::new);
    }

    public MemberDto getMemberDto(Long id) {
        return memberRepository.findDtoById(id).orElseThrow(MemberNotFoundException::new);
    }

    public Page<MemberDto> getMemberDtos(Pageable pageable) {
        return memberRepository.findAllDtos(pageable);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void requestDelete(Member member) {
        member.requestDeletion(LocalDateTime.now());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void cancelDelete(Member member) {
        member.cancelDeletionRequest();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void ban(Member member) {
        member.banByAdmin(LocalDateTime.now());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void unban(Member member) {
        member.unbanByAdmin();
    }
}
