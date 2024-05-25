package org.changppo.account.entity.payment;

import org.changppo.account.builder.member.MemberBuilder;
import org.changppo.account.builder.member.RoleBuilder;
import org.changppo.account.builder.payment.PaymentBuilder;
import org.changppo.account.entity.member.Member;
import org.changppo.account.type.PaymentStatus;
import org.changppo.account.type.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    Payment payment;
    Member member;

    @BeforeEach
    void beforeEach() {
        member = MemberBuilder.buildMember(RoleBuilder.buildRole(RoleType.ROLE_NORMAL));
        payment = PaymentBuilder.buildPayment(member);
    }

    @Test
    void builderTest() {
        // given
        Member testMember = MemberBuilder.buildMember(RoleBuilder.buildRole(RoleType.ROLE_NORMAL));
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        PaymentCardInfo cardInfo = new PaymentCardInfo("type", "issuerCorporation", "bin");

        // when
        Payment testPayment = PaymentBuilder.buildPayment("testKey", new BigDecimal("100.00"), PaymentStatus.COMPLETED_PAID, startTime, endTime, testMember, cardInfo);

        // then
        assertNotNull(testPayment);
        assertEquals("testKey", testPayment.getKey());
        assertEquals(new BigDecimal("100.00"), testPayment.getAmount());
        assertEquals(PaymentStatus.COMPLETED_PAID, testPayment.getStatus());
        assertEquals(startTime, testPayment.getStartedAt());
        assertEquals(endTime, testPayment.getEndedAt());
        assertEquals(testMember, testPayment.getMember());
        assertEquals(cardInfo, testPayment.getCardInfo());
    }

    @Test
    void isDeletedTest() {
        // given
        assertFalse(payment.isDeleted());
        LocalDateTime deletionTime = LocalDateTime.now();

        // when
        ReflectionTestUtils.setField(payment, "deletedAt", deletionTime);

        // then
        assertTrue(payment.isDeleted());
    }

    @Test
    void changeStatusTest() {
        // given
        PaymentStatus newStatus = PaymentStatus.COMPLETED_FREE;
        String newKey = "newKey";
        PaymentCardInfo newCardInfo = new PaymentCardInfo("newType", "newIssuer", "newBin");

        // when
        payment.changeStatus(newStatus, newKey, newCardInfo);

        // then
        assertEquals(newStatus, payment.getStatus());
        assertEquals(newKey, payment.getKey());
        assertEquals(newCardInfo, payment.getCardInfo());
    }
}
