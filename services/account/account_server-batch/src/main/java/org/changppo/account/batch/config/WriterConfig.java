package org.changppo.account.batch.config;

import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.oauth2.OAuth2Client;
import org.changppo.account.paymentgateway.PaymentGatewayClient;
import org.changppo.account.repository.apikey.ApiKeyRepository;
import org.changppo.account.repository.card.CardRepository;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.repository.payment.PaymentRepository;
import org.changppo.account.service.event.payment.PaymentEventPublisher;
import org.changppo.account.type.PaymentGatewayType;
import org.changppo.account.type.PaymentStatus;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WriterConfig {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;
    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final List<OAuth2Client> oauth2Clients;
    private final List<PaymentGatewayClient> paymentGatewayClients;

    @Bean
    public ItemWriter<Payment> paymentWriter() {
        return payments -> payments.forEach(payment -> {
            paymentEventPublisher.publishEvent(payment);
            if (payment.getStatus() == PaymentStatus.FAILED) {
                handlePaymentFailure(payment.getMember());
            } else if (payment.getStatus() == PaymentStatus.COMPLETED_PAID) {
                if (payment.getMember().isDeletionRequested()) {
                    handleMemberDeletion(payment.getMember());
                }
            }
            paymentRepository.save(payment);
        });
    }

    public void handlePaymentFailure(Member member) {
        member.unbanForPaymentFailure();
        apiKeyRepository.unbanApiKeysForPaymentFailure(member.getId());
    }

    public void handleMemberDeletion(Member member) {
        deleteMemberApiKeys(member.getId());
        inactivePaymentGatewayCards(member.getId());
        deleteMemberCards(member.getId());
        deleteMemberPayment(member.getId());
        unlinkOAuth2Member(member.getName());
        memberRepository.delete(member);
    }


    private void deleteMemberApiKeys(Long id) {
        apiKeyRepository.deleteAllByMemberId(id);
    }

    private void inactivePaymentGatewayCards(Long memberId) {
        List<Card> cards = cardRepository.findAllCardByMemberId(memberId);
        cards.forEach(card -> {
            PaymentGatewayType paymentGatewayType = card.getPaymentGateway().getPaymentGatewayType();
            paymentGatewayClients.stream()
                    .filter(client -> client.supports(paymentGatewayType))
                    .findFirst()
                    .orElseThrow(()-> new RuntimeException("Unsupported PaymentGatewayType"))
                    .inactive(card.getKey());
        });
    }

    private void deleteMemberCards(Long id) {
        cardRepository.deleteAllByMemberId(id);
    }

    private void deleteMemberPayment(Long id) {
        paymentRepository.deleteAllByMemberId(id);
    }

    private void unlinkOAuth2Member(String memberName) {
        String[] parts = memberName.split("_");
        if (parts.length < 2) {
            throw new RuntimeException("Invalid member name");
        }
        String provider = parts[0];
        String memberId = parts[1];
        oauth2Clients.stream()
                .filter(service -> service.supports(provider))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unsupported OAuth2 provider"))
                .unlink(memberId);
    }
}
