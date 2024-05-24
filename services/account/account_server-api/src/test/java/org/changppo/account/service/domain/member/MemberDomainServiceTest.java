package org.changppo.account.service.domain.member;

import org.assertj.core.api.Assertions;
import org.changppo.account.builder.member.MemberDtoBuilder;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.response.exception.member.MemberNotFoundException;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.changppo.account.builder.member.MemberBuilder.buildMember;
import static org.changppo.account.builder.member.RoleBuilder.buildRole;
import static org.changppo.account.builder.pageable.PageableBuilder.buildPage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MemberDomainServiceTest {

    @InjectMocks
    MemberDomainService memberDomainService;
    @Mock
    MemberRepository memberRepository;

    Role role;

    @BeforeEach
    void beforeEach() {
        role = buildRole(RoleType.ROLE_NORMAL);
    }

    @Test
    void createMemberTest() {
        // given
        Member member = buildMember(role);
        given(memberRepository.save(any(Member.class))).willReturn(member);

        // when
        Member createdMember = memberDomainService.createMember(member.getName(), member.getUsername(), member.getProfileImage(), member.getRole());

        // then
        Assertions.assertThat(createdMember).isEqualTo(member);
    }

    @Test
    void getMemberTest() {
        // given
        Member member = buildMember(role);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        // when
        Member result = memberDomainService.getMember(1L);

        // then
        assertThat(result).isEqualTo(member);
    }

    @Test
    void getMemberExceptionTest() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> memberDomainService.getMember(1L)).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void getMemberWithRolesTest() {
        // given
        Member member = buildMember(role);
        given(memberRepository.findByIdWithRoles(anyLong())).willReturn(Optional.of(member));

        // when
        Member result = memberDomainService.getMemberWithRoles(1L);

        // then
        assertThat(result).isEqualTo(member);
    }

    @Test
    void getMemberWithRolesExceptionTest() {
        // given
        given(memberRepository.findByIdWithRoles(anyLong())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> memberDomainService.getMemberWithRoles(1L)).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void getOptionalMemberByNameWithRolesTest() {
        // given
        Member member = buildMember(role);
        given(memberRepository.findByNameWithRoles(member.getName())).willReturn(Optional.of(member));

        // when
        Optional<Member> result = memberDomainService.getOptionalMemberByNameWithRoles(member.getName());

        // then
        assertThat(result.get()).isEqualTo(member);
    }

    @Test
    void getMemberDtoTest() {
        // given
        MemberDto memberDto = MemberDtoBuilder.buildMemberDto(role);
        given(memberRepository.findDtoById(anyLong())).willReturn(Optional.of(memberDto));

        // when
        MemberDto result = memberDomainService.getMemberDto(1L);

        // then
        assertThat(result).isEqualTo(memberDto);
    }

    @Test
    void getMemberDtoExceptionTest() {
        // given
        given(memberRepository.findDtoById(anyLong())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> memberDomainService.getMemberDto(1L)).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void getMemberDtoPageTest() {
        // given
        Pageable pageable = buildPage();
        List<MemberDto> memberDtoList = List.of(MemberDtoBuilder.buildMemberDto(role), MemberDtoBuilder.buildMemberDto(role));
        Page<MemberDto> memberDtoPage = new PageImpl<>(memberDtoList, pageable, memberDtoList.size());
        given(memberRepository.findAllDtos(any(PageRequest.class))).willReturn(memberDtoPage);

        // when
        Page<MemberDto> result = memberDomainService.getMemberDtoPage(pageable);

        // then
        assertThat(result.getContent()).isEqualTo(memberDtoList);
    }

    @Test
    void banMemberPaymentFailureTest() {
        // given
        Member member = buildMember(role);

        // when
        memberDomainService.banMemberPaymentFailure(member);

        // then
        assertThat(member.getPaymentFailureBannedAt()).isNotNull();
    }

    @Test
    void unbanMemberPaymentFailureTest() {
        // given
        Member member = buildMember(role);

        // when
        memberDomainService.unbanMemberPaymentFailure(member);

        // then
        assertThat(member.getPaymentFailureBannedAt()).isNull();
    }

    @Test
    void requestMemberDeletionTest() {
        // given
        Member member = buildMember(role);

        // when
        memberDomainService.requestMemberDeletion(member);

        // then
        assertThat(member.getDeletionRequestedAt()).isNotNull();
    }

    @Test
    void cancelMemberDeletionRequestTest() {
        // given
        Member member = buildMember(role);

        // when
        memberDomainService.cancelMemberDeletionRequest(member);

        // then
        assertThat(member.getDeletionRequestedAt()).isNull();
    }

    @Test
    void banMemberByAdminTest() {
        // given
        Member member = buildMember(role);

        // when
        memberDomainService.banMemberByAdmin(member);

        // then
        assertThat(member.getAdminBannedAt()).isNotNull();
    }

    @Test
    void unbanMemberByAdminTest() {
        // given
        Member member = buildMember(role);

        // when
        memberDomainService.unbanMemberByAdmin(member);

        // then
        assertThat(member.getAdminBannedAt()).isNull();
    }
}
