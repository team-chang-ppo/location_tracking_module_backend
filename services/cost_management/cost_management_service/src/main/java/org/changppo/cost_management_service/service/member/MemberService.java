package org.changppo.cost_management_service.service.member;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.dto.member.MemberDto;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.repository.apikey.ApiKeyRepository;
import org.changppo.cost_management_service.repository.card.CardRepository;
import org.changppo.cost_management_service.repository.member.MemberRepository;
import org.changppo.cost_management_service.response.exception.member.MemberNotFoundException;
import org.changppo.cost_management_service.response.exception.member.UnsupportedOAuth2Exception;
import org.changppo.cost_management_service.service.member.oauth2.OAuth2Client;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final List<OAuth2Client> oauth2Clients;
    private final ApiKeyRepository apiKeyRepository;
    private final CardRepository cardRepository;

    public MemberDto read(Long id) {
        Member member = memberRepository.findByIdWithRoles(id).orElseThrow(MemberNotFoundException::new);
        return new MemberDto(member.getId(),member.getName(), member.getUsername(), member.getProfileImage(),
                member.getMemberRoles().stream()
                .map(memberRole -> memberRole.getRole().getRoleType())
                .collect(Collectors.toSet()), member.getIsPaymentFailureBanned(), member.getCreatedAt());
    }

    @Transactional
    @PreAuthorize("@memberAccessEvaluator.check(#id) and @memberPaymentFailureStatusEvaluator.check(#id)") //TODO. 현재 사용자의 남은 요금으로 인한 실패 처리도 해야함
    public void delete(@Param("id")Long id, HttpServletRequest request, HttpServletResponse response) {
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        deleteMemberApiKeys(member.getId());
        deleteMemberCards(member.getId());
        memberRepository.delete(member);
        deleteSession(request);
        deleteCookie(response);
        unlinkOAuth2Member(member.getName());
    }

    private void deleteMemberApiKeys(Long id) {
        apiKeyRepository.deleteAllByMemberId(id);
    }

    private void deleteMemberCards(Long id) {
        cardRepository.deleteAllByMemberId(id);
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

    public void unlinkOAuth2Member(String memberName){
        String[] parts = memberName.split("_");
        if (parts.length < 2) {
            throw new UnsupportedOAuth2Exception();
        }
        String provider = parts[0];
        String MemberId = parts[1];
        oauth2Clients.stream()
                .filter(service -> service.supports(provider))
                .findFirst()
                .orElseThrow(UnsupportedOAuth2Exception::new)
                .unlink(MemberId);
    }
}