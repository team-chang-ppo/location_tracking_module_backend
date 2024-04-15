package org.changppo.cost_management_service.service.payment.batch.config;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.entity.card.Card;
import org.changppo.cost_management_service.entity.card.PaymentGatewayType;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.entity.payment.Payment;
import org.changppo.cost_management_service.entity.payment.PaymentStatus;
import org.changppo.cost_management_service.repository.apikey.ApiKeyRepository;
import org.changppo.cost_management_service.repository.card.CardRepository;
import org.changppo.cost_management_service.repository.member.MemberRepository;
import org.changppo.cost_management_service.repository.payment.PaymentRepository;
import org.changppo.cost_management_service.response.exception.card.UnsupportedPaymentGatewayException;
import org.changppo.cost_management_service.response.exception.member.UnsupportedOAuth2Exception;
import org.changppo.cost_management_service.service.member.oauth2.OAuth2Client;
import org.changppo.cost_management_service.service.paymentgateway.PaymentGatewayClient;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WriterConfig {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final CardRepository cardRepository;
    private final List<OAuth2Client> oauth2Clients;
    private final List<PaymentGatewayClient> paymentGatewayClients;

    @Bean
    public ClassifierCompositeItemWriter<Payment> paymentItemWriter() {
        ClassifierCompositeItemWriter<Payment> writer = new ClassifierCompositeItemWriter<>();
        writer.setClassifier(payment -> {
            if (payment.getStatus() == PaymentStatus.FAILED) {
                return failedPaymentWriter();
            } else if (payment.getMember().isDeletionRequested()) {
                return deletionPaymentWriter();
            } else {
                return normalPaymentWriter();
            }
        });
        return writer;
    }

    @Bean
    public ItemWriter<Payment> failedPaymentWriter() {
        return payments -> payments.forEach(payment -> {
            Member member = payment.getMember();
            handlePaymentFailure(member);
            paymentRepository.save(payment);
        });
    }

    private void handlePaymentFailure(Member member) {
        member.banForPaymentFailure(LocalDateTime.now());
        apiKeyRepository.banApiKeysForPaymentFailure(member.getId(), LocalDateTime.now());
    }

    @Bean
    public ItemWriter<Payment> deletionPaymentWriter() {
        return payments -> payments.forEach(payment -> {
            Member member = payment.getMember();
            deleteMemberApiKeys(member.getId());
            inactivePaymentGatewayCards(member.getId());
            deleteMemberCards(member.getId());
            deleteMemberPayment(member.getId());
            unlinkOAuth2Member(member.getName());
            memberRepository.delete(member);
            payment.setDeletedAt(LocalDateTime.now());
            paymentRepository.save(payment);
        });
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
                    .orElseThrow(UnsupportedPaymentGatewayException::new)
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
            throw new UnsupportedOAuth2Exception();
        }
        String provider = parts[0];
        String memberId = parts[1];
        oauth2Clients.stream()
                .filter(service -> service.supports(provider))
                .findFirst()
                .orElseThrow(UnsupportedOAuth2Exception::new)
                .unlink(memberId);
    }

    @Bean
    public ItemWriter<Payment> normalPaymentWriter() {
        return paymentRepository::saveAll;
    }
}
