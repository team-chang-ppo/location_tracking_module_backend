package org.changppo.account.entity.card;

import org.changppo.account.builder.card.paymentgateway.CardBuilder;
import org.changppo.account.builder.card.paymentgateway.PaymentGatewayBuilder;
import org.changppo.account.builder.member.MemberBuilder;
import org.changppo.account.builder.member.RoleBuilder;
import org.changppo.account.entity.member.Member;
import org.changppo.account.type.PaymentGatewayType;
import org.changppo.account.type.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    Card card;
    Member member;
    PaymentGateway paymentGateway;

    @BeforeEach
    void setUp() {
        member = MemberBuilder.buildMember(RoleBuilder.buildRole(RoleType.ROLE_NORMAL));
        paymentGateway = PaymentGatewayBuilder.buildPaymentGateway(PaymentGatewayType.PG_KAKAOPAY);
        card = CardBuilder.buildCard(member, paymentGateway);
    }

    @Test
    void builderTest() {
        // given
        Member testMember = MemberBuilder.buildMember(RoleBuilder.buildRole(RoleType.ROLE_NORMAL));
        PaymentGateway testPaymentGateway = PaymentGatewayBuilder.buildPaymentGateway(PaymentGatewayType.PG_KAKAOPAY);

        // when
        Card testCard = CardBuilder.buildCard(testMember, testPaymentGateway);

        // then
        assertNotNull(testCard);
        assertEquals("key", testCard.getKey());
        assertEquals("Credit", testCard.getType());
        assertEquals("AcquirerCorp", testCard.getAcquirerCorporation());
        assertEquals("IssuerCorp", testCard.getIssuerCorporation());
        assertEquals("123456", testCard.getBin());
        assertEquals(testPaymentGateway, testCard.getPaymentGateway());
        assertEquals(testMember, testCard.getMember());
    }

    @Test
    void isDeletedTest() {
        // given
        assertFalse(card.isDeleted());
        LocalDateTime deletionTime = LocalDateTime.now();

        // when
        ReflectionTestUtils.setField(card, "deletedAt", deletionTime);

        // then
        assertTrue(card.isDeleted());
    }
}

