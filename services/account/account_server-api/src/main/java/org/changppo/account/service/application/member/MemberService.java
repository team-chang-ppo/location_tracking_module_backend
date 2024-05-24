package org.changppo.account.service.application.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.member.Member;
import org.changppo.account.service.domain.apikey.ApiKeyDomainService;
import org.changppo.account.service.domain.member.MemberDomainService;
import org.changppo.account.service.domain.member.SessionDomainService;
import org.changppo.account.service.dto.member.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberDomainService memberDomainService;
    private final ApiKeyDomainService apiKeyDomainService;
    private final SessionDomainService sessionDomainService;

    @PreAuthorize("@memberAccessEvaluator.check(#id)")
    public MemberDto read(@Param("id")Long id) {
        return memberDomainService.getMemberDto(id);
    }

    public Page<MemberDto> readList(Pageable pageable) {
        return memberDomainService.getMemberDtoPage(pageable);
    }

    @Transactional
    @PreAuthorize("@memberAccessEvaluator.check(#id) and @memberNotPaymentFailureStatusEvaluator.check(#id)")
    public void requestDelete(@Param("id")Long id, HttpServletRequest request, HttpServletResponse response) {
        Member member = memberDomainService.getMember(id);
        memberDomainService.requestMemberDeletion(member);
        apiKeyDomainService.requestApiKeyDeletion(id);
        sessionDomainService.invalidateSessionAndClearCookies(request, response);
    }

    @Transactional
    public void cancelDelete(@Param("id")Long id) {
        Member member = memberDomainService.getMember(id);
        memberDomainService.cancelMemberDeletionRequest(member);
        apiKeyDomainService.cancelApiKeyDeletionRequest(id);
    }

    @Transactional
    public void ban(@Param("id")Long id) {
        Member member = memberDomainService.getMember(id);
        memberDomainService.banMemberByAdmin(member);
        apiKeyDomainService.banApiKeysByAdmin(id);
        sessionDomainService.expireSessions(member.getName());
    }

    @Transactional
    public void unban(@Param("id")Long id) {
        Member member = memberDomainService.getMember(id);
        memberDomainService.unbanMemberByAdmin(member);
        apiKeyDomainService.unbanApiKeysByAdmin(id);
    }
}
