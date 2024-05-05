package org.changppo.account.service.member;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.member.Member;
import org.changppo.account.repository.apikey.ApiKeyRepository;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.response.exception.member.MemberNotFoundException;
import org.changppo.account.service.dto.member.MemberDto;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final ApiKeyRepository apiKeyRepository;

    @PreAuthorize("@memberAccessEvaluator.check(#id)")
    public MemberDto read(@Param("id")Long id) {
        Member member = memberRepository.findByIdWithRoles(id).orElseThrow(MemberNotFoundException::new);
        return new MemberDto(member.getId(),member.getName(), member.getUsername(), member.getProfileImage(),
                member.getMemberRoles().stream()
                .map(memberRole -> memberRole.getRole().getRoleType())
                .collect(Collectors.toSet()), member.getPaymentFailureBannedAt(),
                member.getDeletionRequestedAt(), member.getCreatedAt());
    }

    @Transactional
    @PreAuthorize("@memberAccessEvaluator.check(#id) and @memberPaymentFailureStatusEvaluator.check(#id) and @memberDeletionRequestedStatusEvaluator.check(#id)")
    public MemberDto requestDelete(@Param("id")Long id, HttpServletRequest request, HttpServletResponse response) {
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        member.requestDeletion(LocalDateTime.now());
        apiKeyRepository.requestApiKeyDeletion(id, LocalDateTime.now());
        deleteSession(request);
        deleteCookie(response);
        return new MemberDto(member.getId(),member.getName(), member.getUsername(), member.getProfileImage(),
                member.getMemberRoles().stream()
                        .map(memberRole -> memberRole.getRole().getRoleType())
                        .collect(Collectors.toSet()), member.getPaymentFailureBannedAt(),
                member.getDeletionRequestedAt(), member.getCreatedAt());
    }

    public void deleteSession(HttpServletRequest request){
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public void deleteCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        // refreshCookie.setSecure(true);
        response.addCookie(cookie);
    }

    @Transactional
    @PreAuthorize("@memberAccessEvaluator.check(#id)")
    public MemberDto cancelDelete(@Param("id")Long id) {
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        member.cancelDeletionRequest();
        apiKeyRepository.cancelApiKeyDeletionRequest(id);
        return new MemberDto(member.getId(),member.getName(), member.getUsername(), member.getProfileImage(),
                member.getMemberRoles().stream()
                        .map(memberRole -> memberRole.getRole().getRoleType())
                        .collect(Collectors.toSet()), member.getPaymentFailureBannedAt(),
                member.getDeletionRequestedAt(), member.getCreatedAt());
    }
}
