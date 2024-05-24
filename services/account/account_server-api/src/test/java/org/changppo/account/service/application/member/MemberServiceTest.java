package org.changppo.account.service.application.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;
import org.changppo.account.service.domain.apikey.ApiKeyDomainService;
import org.changppo.account.service.domain.member.MemberDomainService;
import org.changppo.account.service.domain.member.SessionDomainService;
import org.changppo.account.service.dto.member.MemberDto;
import org.changppo.account.type.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.changppo.account.builder.member.MemberBuilder.buildMember;
import static org.changppo.account.builder.member.MemberDtoBuilder.buildMemberDto;
import static org.changppo.account.builder.member.RoleBuilder.buildRole;
import static org.changppo.account.builder.pageable.PageableBuilder.buildPage;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;
    @Mock
    private MemberDomainService memberDomainService;
    @Mock
    private ApiKeyDomainService apiKeyDomainService;
    @Mock
    private SessionDomainService sessionDomainService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    Role role;

    @BeforeEach
    void beforeEach() {
        role = buildRole(RoleType.ROLE_NORMAL);
    }

    @Test
    void readTest() {
        // given
        MemberDto memberDto = buildMemberDto(role);
        given(memberDomainService.getMemberDto(anyLong())).willReturn(memberDto);

        // when
        MemberDto result = memberService.read(1L);

        // then
        assertThat(result).isEqualTo(memberDto);
    }

    @Test
    void readListTest() {
        // given
        Pageable pageable = buildPage();
        MemberDto memberDto = buildMemberDto(role);
        List<MemberDto> memberDtos = List.of(memberDto, memberDto);
        Page<MemberDto> page = new PageImpl<>(memberDtos, pageable, 2);
        given(memberDomainService.getMemberDtoPage(any(Pageable.class))).willReturn(page);

        // when
        Page<MemberDto> result = memberService.readList(pageable);

        // then
        assertThat(result.getContent()).isEqualTo(memberDtos);
    }

    @Test
    void requestDeleteTest() {
        // given
        Member member = buildMember(role);
        given(memberDomainService.getMember(anyLong())).willReturn(member);
        doNothing().when(memberDomainService).requestMemberDeletion(any(Member.class));
        doNothing().when(apiKeyDomainService).requestApiKeyDeletion(anyLong());
        doNothing().when(sessionDomainService).invalidateSessionAndClearCookies(any(HttpServletRequest.class), any(HttpServletResponse.class));

        // when
        memberService.requestDelete(1L, request, response);

        // then
        verify(memberDomainService).requestMemberDeletion(member);
        verify(apiKeyDomainService).requestApiKeyDeletion(1L);
        verify(sessionDomainService).invalidateSessionAndClearCookies(request, response);
    }

    @Test
    void cancelDeleteTest() {
        // given
        Member member = buildMember(role);
        given(memberDomainService.getMember(anyLong())).willReturn(member);
        doNothing().when(memberDomainService).cancelMemberDeletionRequest(any(Member.class));
        doNothing().when(apiKeyDomainService).cancelApiKeyDeletionRequest(anyLong());

        // when
        memberService.cancelDelete(1L);

        // then
        verify(memberDomainService).cancelMemberDeletionRequest(member);
        verify(apiKeyDomainService).cancelApiKeyDeletionRequest(1L);
    }

    @Test
    void banTest() {
        // given
        Member member = buildMember(role);
        given(memberDomainService.getMember(anyLong())).willReturn(member);
        doNothing().when(memberDomainService).banMemberByAdmin(any(Member.class));
        doNothing().when(apiKeyDomainService).banApiKeysByAdmin(anyLong());
        doNothing().when(sessionDomainService).expireSessions(anyString());

        // when
        memberService.ban(1L);

        // then
        verify(memberDomainService).banMemberByAdmin(member);
        verify(apiKeyDomainService).banApiKeysByAdmin(1L);
        verify(sessionDomainService).expireSessions(member.getName());
    }

    @Test
    void unbanTest() {
        // given
        Member member = buildMember(role);
        given(memberDomainService.getMember(anyLong())).willReturn(member);
        doNothing().when(memberDomainService).unbanMemberByAdmin(any(Member.class));
        doNothing().when(apiKeyDomainService).unbanApiKeysByAdmin(anyLong());

        // when
        memberService.unban(1L);

        // then
        verify(memberDomainService).unbanMemberByAdmin(member);
        verify(apiKeyDomainService).unbanApiKeysByAdmin(1L);
    }
}
