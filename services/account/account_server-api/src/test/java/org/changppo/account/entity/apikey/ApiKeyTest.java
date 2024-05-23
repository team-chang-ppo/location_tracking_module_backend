package org.changppo.account.entity.apikey;

import org.changppo.account.builder.apikey.ApiKeyBuilder;
import org.changppo.account.builder.apikey.GradeBuilder;
import org.changppo.account.builder.member.MemberBuilder;
import org.changppo.account.builder.member.RoleBuilder;
import org.changppo.account.entity.member.Member;
import org.changppo.account.type.GradeType;
import org.changppo.account.type.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ApiKeyTest {

    ApiKey apiKey;
    Grade grade;
    Member member;

    @BeforeEach
    void setUp() {
        grade = GradeBuilder.buildGrade(GradeType.GRADE_CLASSIC);
        member = MemberBuilder.buildMember(RoleBuilder.buildRole(RoleType.ROLE_NORMAL));
        apiKey = ApiKeyBuilder.buildApiKey(grade, member);
    }

    @Test
    void builderTest() {
        // given
        Grade testGrade = GradeBuilder.buildGrade(GradeType.GRADE_CLASSIC);
        Member testMember = MemberBuilder.buildMember(RoleBuilder.buildRole(RoleType.ROLE_NORMAL));

        // when
        ApiKey testApiKey = ApiKeyBuilder.buildApiKey(testGrade, testMember);

        // then
        assertNotNull(testApiKey);
        assertEquals("testApiKeyValue", testApiKey.getValue());
        assertEquals(testGrade, testApiKey.getGrade());
        assertEquals(testMember, testApiKey.getMember());
    }

    @Test
    void isDeletedTest() {
        // given
        assertFalse(apiKey.isDeleted());
        LocalDateTime deletionTime = LocalDateTime.now();

        // when
        ReflectionTestUtils.setField(apiKey, "deletedAt", deletionTime);

        // then
        assertTrue(apiKey.isDeleted());
    }

    @Test
    void isPaymentFailureBannedTest() {
        // given
        assertFalse(apiKey.isPaymentFailureBanned());
        LocalDateTime banTime = LocalDateTime.now();

        // when
        apiKey.banForPaymentFailure(banTime);

        // then
        assertTrue(apiKey.isPaymentFailureBanned());
    }

    @Test
    void isCardDeletionBannedTest() {
        // given
        assertFalse(apiKey.isCardDeletionBanned());
        LocalDateTime banTime = LocalDateTime.now();

        // when
        apiKey.banForCardDeletion(banTime);

        // then
        assertTrue(apiKey.isCardDeletionBanned());
    }

    @Test
    void isDeletionRequestedTest() {
        // given
        assertFalse(apiKey.isDeletionRequested());
        LocalDateTime requestTime = LocalDateTime.now();

        // when
        apiKey.requestDeletion(requestTime);

        // then
        assertTrue(apiKey.isDeletionRequested());
    }

    @Test
    void isAdminBannedTest() {
        // given
        assertFalse(apiKey.isAdminBanned());
        LocalDateTime banTime = LocalDateTime.now();

        // when
        apiKey.banByAdmin(banTime);

        // then
        assertTrue(apiKey.isAdminBanned());
    }

    @Test
    void updateValueTest() {
        // given
        String newValue = "newApiKeyValue";

        // when
        apiKey.updateValue(newValue);

        // then
        assertEquals(newValue, apiKey.getValue());
    }

    @Test
    void banForPaymentFailureTest() {
        // given
        LocalDateTime banTime = LocalDateTime.now();

        // when
        apiKey.banForPaymentFailure(banTime);

        // then
        assertEquals(banTime, apiKey.getPaymentFailureBannedAt());
    }

    @Test
    void unbanForPaymentFailureTest() {
        // given
        apiKey.banForPaymentFailure(LocalDateTime.now());

        // when
        apiKey.unbanForPaymentFailure();

        // then
        assertNull(apiKey.getPaymentFailureBannedAt());
    }

    @Test
    void banForCardDeletionTest() {
        // given
        LocalDateTime banTime = LocalDateTime.now();

        // when
        apiKey.banForCardDeletion(banTime);

        // then
        assertEquals(banTime, apiKey.getCardDeletionBannedAt());
    }

    @Test
    void unbanForCardDeletionTest() {
        // given
        apiKey.banForCardDeletion(LocalDateTime.now());

        // when
        apiKey.unbanForCardDeletion();

        // then
        assertNull(apiKey.getCardDeletionBannedAt());
    }

    @Test
    void requestDeletionTest() {
        // given
        LocalDateTime requestTime = LocalDateTime.now();

        // when
        apiKey.requestDeletion(requestTime);

        // then
        assertEquals(requestTime, apiKey.getDeletionRequestedAt());
    }

    @Test
    void cancelDeletionRequestTest() {
        // given
        apiKey.requestDeletion(LocalDateTime.now());

        // when
        apiKey.cancelDeletionRequest();

        // then
        assertNull(apiKey.getDeletionRequestedAt());
    }

    @Test
    void banByAdminTest() {
        // given
        LocalDateTime banTime = LocalDateTime.now();

        // when
        apiKey.banByAdmin(banTime);

        // then
        assertEquals(banTime, apiKey.getAdminBannedAt());
    }

    @Test
    void unbanByAdminTest() {
        // given
        apiKey.banByAdmin(LocalDateTime.now());

        // when
        apiKey.unbanByAdmin();

        // then
        assertNull(apiKey.getAdminBannedAt());
    }
}
