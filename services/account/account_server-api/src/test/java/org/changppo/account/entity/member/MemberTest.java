package org.changppo.account.entity.member;

import org.changppo.account.builder.member.MemberBuilder;
import org.changppo.account.builder.member.RoleBuilder;
import org.changppo.account.type.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    Member member;
    Role role;

    @BeforeEach
    void beforeEach() {
        role = RoleBuilder.buildRole(RoleType.ROLE_NORMAL);
        member = MemberBuilder.buildMember(role);
    }

    @Test
    void builderTest() {
        // given
        Role testRole = RoleBuilder.buildRole(RoleType.ROLE_NORMAL);

        // when
        Member testMember = MemberBuilder.buildMember(testRole);

        // then
        assertNotNull(testMember);
        assertEquals("testName", testMember.getName());
        assertEquals("testUsername", testMember.getUsername());
        assertEquals("testProfileImage", testMember.getProfileImage());
        assertEquals(testRole, testMember.getRole());
    }

    @Test
    void isDeletedTest() {
        // given
        assertFalse(member.isDeleted());
        LocalDateTime deletionTime = LocalDateTime.now();

        // when
        ReflectionTestUtils.setField(member, "deletedAt", deletionTime);

        // then
        assertTrue(member.isDeleted());
    }

    @Test
    void isPaymentFailureBannedTest() {
        // given
        assertFalse(member.isPaymentFailureBanned());
        LocalDateTime banTime = LocalDateTime.now();

        // when
        member.banForPaymentFailure(banTime);

        // then
        assertTrue(member.isPaymentFailureBanned());
    }

    @Test
    void isDeletionRequestedTest() {
        // given
        assertFalse(member.isDeletionRequested());
        LocalDateTime requestTime = LocalDateTime.now();

        // when
        member.requestDeletion(requestTime);

        // then
        assertTrue(member.isDeletionRequested());
    }

    @Test
    void isAdminBannedTest() {
        // given
        assertFalse(member.isAdminBanned());
        LocalDateTime banTime = LocalDateTime.now();

        // when
        member.banByAdmin(banTime);

        // then
        assertTrue(member.isAdminBanned());
    }

    @Test
    void updateInfoTest() {
        // given
        String newUsername = "newUsername";
        String newProfileImage = "newProfileImage.jpg";

        // when
        member.updateInfo(newUsername, newProfileImage);

        // then
        assertEquals(newUsername, member.getUsername());
        assertEquals(newProfileImage, member.getProfileImage());
    }

    @Test
    void changeRoleTest() {
        // given
        Role newRole = RoleBuilder.buildRole(RoleType.ROLE_ADMIN);

        // when
        member.changeRole(newRole);

        // then
        assertEquals(newRole, member.getRole());
    }

    @Test
    void banForPaymentFailureTest() {
        // given
        LocalDateTime banTime = LocalDateTime.now();

        // when
        member.banForPaymentFailure(banTime);

        // then
        assertEquals(banTime, member.getPaymentFailureBannedAt());
    }

    @Test
    void unbanForPaymentFailureTest() {
        // given
        member.banForPaymentFailure(LocalDateTime.now());

        // when
        member.unbanForPaymentFailure();

        // then
        assertNull(member.getPaymentFailureBannedAt());
    }

    @Test
    void requestDeletionTest() {
        // given
        LocalDateTime requestTime = LocalDateTime.now();

        // when
        member.requestDeletion(requestTime);

        // then
        assertEquals(requestTime, member.getDeletionRequestedAt());
    }

    @Test
    void cancelDeletionRequestTest() {
        // given
        member.requestDeletion(LocalDateTime.now());

        // when
        member.cancelDeletionRequest();

        // then
        assertNull(member.getDeletionRequestedAt());
    }

    @Test
    void banByAdminTest() {
        // given
        LocalDateTime banTime = LocalDateTime.now();

        // when
        member.banByAdmin(banTime);

        // then
        assertEquals(banTime, member.getAdminBannedAt());
    }

    @Test
    void unbanByAdminTest() {
        // given
        member.banByAdmin(LocalDateTime.now());

        // when
        member.unbanByAdmin();

        // then
        assertNull(member.getAdminBannedAt());
    }
}
