package org.changppo.account.batch.config;

import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.oauth2.OAuth2Client;
import org.changppo.account.paymentgateway.PaymentGatewayClient;
import org.changppo.account.repository.apikey.ApiKeyRepository;
import org.changppo.account.repository.card.CardRepository;
import org.changppo.account.repository.payment.PaymentRepository;
import org.changppo.account.service.event.payment.PaymentEventPublisher;
import org.changppo.account.type.PaymentGatewayType;
import org.changppo.account.type.PaymentStatus;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WriterConfig {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;
    private final ApiKeyRepository apiKeyRepository;
    private final CardRepository cardRepository;
    private final List<PaymentGatewayClient> paymentGatewayClients;
    private final List<OAuth2Client> oauth2Clients;

    @Bean
    public ItemWriter<Payment> paymentItemWriterForAutomaticPayment() {
        return payments -> payments.forEach(payment -> {
            if (payment.getStatus() == PaymentStatus.FAILED) {
                handlePaymentFailure(payment.getMember());
            }
            paymentEventPublisher.publishEvent(payment);
            paymentRepository.save(payment);
        });
    }

    @Bean
    public ItemWriter<Payment> paymentItemWriterForDeletion() {
        return payments -> payments.forEach(payment -> {
            if (payment.getStatus() == PaymentStatus.FAILED) {
                handlePaymentFailure(payment.getMember());
                handleMemberDeletionFailure(payment.getMember());
                paymentRepository.save(payment);
                paymentEventPublisher.publishEvent(payment);
            }
            else {
                handleMemberDeletion(payment.getMember());
            }
        });
    }

    public void handlePaymentFailure(Member member) {
        member.banForPaymentFailure(LocalDateTime.now());
        apiKeyRepository.unbanApiKeysForPaymentFailure(member.getId());
    }

    public void handleMemberDeletionFailure(Member member) {
        member.cancelDeletionRequest();
        apiKeyRepository.cancelApiKeyDeletionRequest(member.getId());
    }

    public void handleMemberDeletion(Member member) {
        deleteMemberApiKeys(member.getId());
        deleteMemberCards(member.getId());
        deleteMemberPayment(member.getId());
        inactivePaymentGatewayCards(member.getId());
        unlinkOAuth2Member(member.getName());
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
                    .orElseThrow(()-> new RuntimeException("Unsupported Payment Gateway"))
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
            throw new RuntimeException("Unsupported OAuth2");
        }
        String provider = parts[0];
        String memberId = parts[1];
        oauth2Clients.stream()
                .filter(service -> service.supports(provider))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Unsupported OAuth2"))
                .unlink(memberId);
    }
}
