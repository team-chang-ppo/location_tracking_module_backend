package org.changppo.account.repository.member;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.changppo.account.builder.pageable.PageableBuilder;
import org.changppo.account.config.QuerydslConfig;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;
import org.changppo.account.response.exception.member.MemberNotFoundException;
import org.changppo.account.service.dto.member.MemberDto;
import org.changppo.account.type.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.changppo.account.builder.member.MemberBuilder.buildMember;
import static org.changppo.account.builder.member.RoleBuilder.buildRole;

@DataJpaTest
@Import(QuerydslConfig.class)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RoleRepository roleRepository;
    @PersistenceContext
    EntityManager em;

    Role role;

    @BeforeEach
    void beforeEach() {
        role = roleRepository.save(buildRole(RoleType.ROLE_NORMAL));
    }


    @Test
    void creatTest() {
        // given
        Member member = buildMember(role);
        // when
        memberRepository.save(member);
        clear();

        // then
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        assertThat(foundMember.getId()).isEqualTo(member.getId());
    }

    @Test
    void uniqueNameTest() {
        // given
        memberRepository.save(buildMember("name", "username", "profileImage", role));
        clear();

        // when, then
        assertThatThrownBy(() -> memberRepository.save(buildMember("name", "username", "profileImage", role)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void dateTest() {
        // given
        Member member = buildMember(role);

        // when
        memberRepository.save(member);
        clear();

        // then
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        assertThat(foundMember.getCreatedAt()).isNotNull();
        assertThat(foundMember.getModifiedAt()).isNotNull();
        assertThat(foundMember.getCreatedAt()).isEqualTo(foundMember.getModifiedAt());
    }

    @Test
    void updateTest() {
        // given
        String updatedUsername = "updatedUsername";
        String updatedProfileImage = "updatedProfileImage";
        Member member = memberRepository.save(buildMember(role));
        clear();

        // when
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        foundMember.updateInfo(updatedUsername, updatedProfileImage);
        clear();

        // then
        Member updatedMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        assertThat(updatedMember.getUsername()).isEqualTo(updatedUsername);
        assertThat(updatedMember.getProfileImage()).isEqualTo(updatedProfileImage);
    }

    @Test
    void deleteTest() {
        // given
        Member member = memberRepository.save(buildMember(role));
        clear();

        // when
        memberRepository.delete(member);
        clear();

        // then
        assertThatThrownBy(() -> memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void changeRoleTest() {
        // given
        Role newRole = roleRepository.save(new Role(RoleType.ROLE_ADMIN));
        Member member = memberRepository.save(buildMember(role));
        clear();

        // when
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        foundMember.changeRole(newRole);
        memberRepository.save(foundMember);
        clear();

        // then
        Member updatedMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        assertThat(updatedMember.getRole().getRoleType()).isEqualTo(newRole.getRoleType());
    }

    @Test
    void banAndUnbanForPaymentFailureTest() {
        // given
        LocalDateTime banTime = LocalDateTime.now();
        Member member = memberRepository.save(buildMember(role));
        clear();

        // when
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        foundMember.banForPaymentFailure(banTime);
        memberRepository.save(foundMember);
        clear();

        // then
        Member bannedMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        assertThat(bannedMember.isPaymentFailureBanned()).isTrue();

        // when
        bannedMember.unbanForPaymentFailure();
        memberRepository.save(bannedMember);
        clear();

        // then
        Member unbannedMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        assertThat(unbannedMember.isPaymentFailureBanned()).isFalse();
    }

    @Test
    void deletionRequestAndCancelTest() {
        // given
        LocalDateTime requestTime = LocalDateTime.now();
        Member member = memberRepository.save(buildMember(role));
        clear();

        // when
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        foundMember.requestDeletion(requestTime);
        memberRepository.save(foundMember);
        clear();

        // then
        Member deletionRequestedMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        assertThat(deletionRequestedMember.isDeletionRequested()).isTrue();

        // when
        deletionRequestedMember.cancelDeletionRequest();
        memberRepository.save(deletionRequestedMember);
        clear();

        // then
        Member cancellationRequestedMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        assertThat(cancellationRequestedMember.isDeletionRequested()).isFalse();
    }

    @Test
    void adminBanAndUnbanTest() {
        // given
        LocalDateTime banTime = LocalDateTime.now();
        Member member = memberRepository.save(buildMember(role));
        clear();

        // when
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        foundMember.banByAdmin(banTime);
        memberRepository.save(foundMember);
        clear();

        // then
        Member bannedMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        assertThat(bannedMember.isAdminBanned()).isTrue();

        // when
        bannedMember.unbanByAdmin();
        memberRepository.save(bannedMember);
        clear();

        // then
        Member unbannedMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        assertThat(unbannedMember.isAdminBanned()).isFalse();
    }

    @Test
    void findByNameWithRolesTest() {
        // given
        Member member = memberRepository.save(buildMember(role));
        clear();

        // when
        Member foundMember = memberRepository.findByNameWithRoles(member.getName()).orElseThrow(MemberNotFoundException::new);

        // then
        assertThat(foundMember.getName()).isEqualTo(member.getName());
        assertThat(foundMember.getRole().getRoleType()).isEqualTo(RoleType.ROLE_NORMAL);
    }

    @Test
    void findByIdWithRolesTest() {
        // given
        Member member = memberRepository.save(buildMember(role));
        clear();

        // when
        Member foundMember = memberRepository.findByIdWithRoles(member.getId()).orElseThrow(MemberNotFoundException::new);

        // then
        assertThat(foundMember.getId()).isEqualTo(member.getId());
        assertThat(foundMember.getRole().getRoleType()).isEqualTo(RoleType.ROLE_NORMAL);
    }

    @Test
    void findDtoByIdTest() {
        // given
        Member member = memberRepository.save(buildMember(role));
        clear();

        // when
        MemberDto memberDto = memberRepository.findDtoById(member.getId()).orElseThrow(MemberNotFoundException::new);

        // then
        assertThat(memberDto.getId()).isEqualTo(member.getId());
        assertThat(memberDto.getName()).isEqualTo(member.getName());
    }

    @Test
    void findAllDtosTest() {
        // given
        memberRepository.save(buildMember("testName1", "username1", "profileImage1", role));
        memberRepository.save(buildMember("testName2", "username2", "profileImage2", role));
        clear();

        // when
        Pageable pageable = PageableBuilder.buildPage();
        Page<MemberDto> memberDtos = memberRepository.findAllDtos(pageable);

        // then
        assertThat(memberDtos).isNotEmpty();
        assertThat(memberDtos.getContent().size()).isEqualTo(2);
    }

    private void clear() {
        em.flush();
        em.clear();
    }
}
