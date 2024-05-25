package org.changppo.account.builder.card;

import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.card.PaymentGateway;
import org.changppo.account.entity.member.Member;

public class CardBuilder {
    public static Card buildCard(Member member, PaymentGateway paymentGateway) {
        return Card.builder()
                .key("key")
                .type("Credit")
                .acquirerCorporation("AcquirerCorp")
                .issuerCorporation("IssuerCorp")
                .bin("123456")
                .paymentGateway(paymentGateway)
                .member(member)
                .build();
    }

    public static Card buildCard(String key, Member member, PaymentGateway paymentGateway) {
        return Card.builder()
                .key(key)
                .type("Credit")
                .acquirerCorporation("AcquirerCorp")
                .issuerCorporation("IssuerCorp")
                .bin("123456")
                .paymentGateway(paymentGateway)
                .member(member)
                .build();
    }
}
