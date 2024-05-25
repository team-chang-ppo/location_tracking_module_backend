package org.changppo.account.builder.payment;

import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.entity.payment.PaymentCardInfo;
import org.changppo.account.type.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentBuilder {

    public static Payment buildPayment(Member member) {
        return Payment.builder()
                .key("testKey")
                .amount(new BigDecimal("100.00"))
                .status(PaymentStatus.COMPLETED_PAID)
                .startedAt(LocalDate.now())
                .endedAt(LocalDate.now().plusDays(1))
                .member(member)
                .cardInfo(new PaymentCardInfo("type", "issuerCorporation", "bin"))
                .build();
    }

    public static Payment buildPayment(String key, BigDecimal amount, PaymentStatus status, LocalDate startedAt, LocalDate endedAt, Member member, PaymentCardInfo cardInfo) {
        return Payment.builder()
                .key(key)
                .amount(amount)
                .status(status)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .member(member)
                .cardInfo(cardInfo)
                .build();
    }
}
