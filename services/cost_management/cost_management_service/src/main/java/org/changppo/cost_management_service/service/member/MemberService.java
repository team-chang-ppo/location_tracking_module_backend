package org.changppo.cost_management_service.service.member;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.dto.member.MemberDto;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.exception.MemberNotFoundException;
import org.changppo.cost_management_service.exception.MemberUnlinkFailureException;
import org.changppo.cost_management_service.repository.apikey.ApiKeyRepository;
import org.changppo.cost_management_service.repository.member.MemberRepository;
import org.changppo.cost_management_service.service.member.oauth.OAuth2Service;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final List<OAuth2Service> oauth2Services;
    private final ApiKeyRepository apiKeyRepository;

    public MemberDto read(Long id) {
        Member member = memberRepository.findByIdWithRoles(id).orElseThrow(MemberNotFoundException::new);
        return new MemberDto(member.getId(),member.getName(), member.getUsername(), member.getProfileImage(),
                member.getRoles().stream()
                .map(memberRole -> memberRole.getRole().getRoleType())
                .collect(Collectors.toSet()), member.getBannedAt(), member.getCreatedAt());
    }

    @Transactional
    @PreAuthorize("@memberAccessEvaluator.check(#id) and @memberStatusEvaluator.check(#id)")
    public void delete(@Param("id")Long id, HttpServletRequest request, HttpServletResponse response) {
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        try {
            unlinkSocialAccount(member.getName());
        } catch (Exception e) {
            throw new MemberUnlinkFailureException(e);
        }
        deleteSession(request);
        deleteCookie(response);
        deleteMemberApiKeys(member.getId());
        memberRepository.delete(member);
    }

    public void unlinkSocialAccount(String memberName) throws IOException {
        String[] parts = memberName.split("_");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid member name format");
        }
        String provider = parts[0];
        String providerMemberId = parts[1];
        oauth2Services.stream()
                .filter(service -> service.supports(provider))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported provider: " + provider))
                .unlinkMember(providerMemberId);
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

    private void deleteMemberApiKeys(Long id) {
        apiKeyRepository.deleteAllByMemberId(id);
    }
}