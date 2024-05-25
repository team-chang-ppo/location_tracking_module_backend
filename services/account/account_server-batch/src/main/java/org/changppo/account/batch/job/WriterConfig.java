package org.changppo.account.batch.job;

import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.oauth2.OAuth2AuthorizedClient;
import org.changppo.account.entity.member.oauth2.OAuth2AuthorizedClientId;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.oauth2.OAuth2Client;
import org.changppo.account.paymentgateway.PaymentGatewayClient;
import org.changppo.account.repository.apikey.ApiKeyRepository;
import org.changppo.account.repository.card.CardRepository;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.repository.member.oauth2.OAuth2AuthorizedClientRepository;
import org.changppo.account.repository.payment.PaymentRepository;
import org.changppo.account.service.event.payment.PaymentEventPublisher;
import org.changppo.account.type.PaymentGatewayType;
import org.changppo.account.type.PaymentStatus;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

import static org.changppo.account.oauth2.github.GitHubConstants.GITHUB_REGISTRATION_ID;
import static org.changppo.account.oauth2.kakao.KakaoConstants.KAKAO_REGISTRATION_ID;

@Configuration
@RequiredArgsConstructor
public class WriterConfig {

    public static final String AUTOMATIC_PAYMENT_WRITER = "paymentItemWriterForAutomaticPayment";
    public static final String DELETION_WRITER = "paymentItemWriterForDeletion";

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;
    private final MemberRepository memberRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final CardRepository cardRepository;
    private final List<PaymentGatewayClient> paymentGatewayClients;
    private final List<OAuth2Client> oauth2Clients;
    private final OAuth2AuthorizedClientRepository authorizedClientRepository;

    @Bean(AUTOMATIC_PAYMENT_WRITER)
    @StepScope
    public ItemWriter<Payment> paymentItemWriterForAutomaticPayment(@Value("#{jobParameters[JobStartTime]}") LocalDateTime jobStartTime) {
        return payments -> payments.forEach(payment -> {
            paymentEventPublisher.publishEvent(payment);
            paymentRepository.save(payment);
            if (payment.getStatus() == PaymentStatus.FAILED) {
                handleAutomaticPaymentFailure(payment.getMember(), jobStartTime);
            }
        });
    }

    public void handleAutomaticPaymentFailure(Member member, LocalDateTime jobStartTime) {
        member.banForPaymentFailure(jobStartTime);
        apiKeyRepository.banApiKeysForPaymentFailureByMemberId(member.getId(), jobStartTime);
        memberRepository.save(member);
    }

    @Bean(DELETION_WRITER)
    @StepScope
    public ItemWriter<Payment> paymentItemWriterForDeletion(@Value("#{jobParameters[JobStartTime]}") LocalDateTime jobStartTime) {
        return payments -> payments.forEach(payment -> {
            paymentRepository.save(payment);
            paymentEventPublisher.publishEvent(payment);
            if (payment.getStatus() == PaymentStatus.FAILED) {
                handleDeletionPaymentFailure(payment.getMember(), jobStartTime);
            }
            else {
                handleMemberDeletion(payment.getMember());
            }
        });
    }

    public void handleDeletionPaymentFailure(Member member, LocalDateTime jobStartTime) {
        member.banForPaymentFailure(jobStartTime);
        apiKeyRepository.banApiKeysForPaymentFailureByMemberId(member.getId(), jobStartTime);
        member.cancelDeletionRequest();
        apiKeyRepository.cancelApiKeyDeletionRequestByMemberId(member.getId());
        memberRepository.save(member);
    }

    public void handleMemberDeletion(Member member) {
        deleteMemberApiKeys(member.getId());
        inactivePaymentGatewayCards(member.getId());
        deleteMemberCards(member.getId());
        deleteMemberPayment(member.getId());
        unlinkOAuth2Member(member.getName());
        deleteMember(member);
    }

    private void deleteMemberApiKeys(Long id) {
        apiKeyRepository.deleteAllByMemberId(id);
    }

    private void inactivePaymentGatewayCards(Long memberId) {
        List<Card> cards = cardRepository.findAllCardByMemberIdOrderByAsc(memberId);
        cards.forEach(card -> {
            PaymentGatewayType paymentGatewayType = card.getPaymentGateway().getPaymentGatewayType();
            paymentGatewayClients.stream()
                    .filter(client -> client.supports(paymentGatewayType))
                    .findFirst()
                    .orElseThrow(()-> new RuntimeException("Unsupported Payment Gateway: " + paymentGatewayType))
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
        String[] parts = validateMemberName(memberName);
        String providerName = createProviderName(parts);
        String clientRegistrationId = parts[1].toLowerCase();
        String memberId = parts[2];

        OAuth2AuthorizedClient authorizedClient = authorizedClientRepository.findById(new OAuth2AuthorizedClientId(clientRegistrationId, memberName)).orElseThrow(() -> new RuntimeException("No OAuth2 client found for the given provider and member."));
        String identifier = getIdentifier(clientRegistrationId, memberId, authorizedClient);

        oauth2Clients.stream()
                .filter(service -> service.supports(providerName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unsupported OAuth2 provider: " + providerName))
                .unlink(identifier);

        authorizedClientRepository.delete(authorizedClient);
    }

    private String[] validateMemberName(String memberName) {
        String[] parts = memberName.split("_", 3);
        if (parts.length < 3) {
            throw new RuntimeException("Invalid member name format for OAuth2 unlinking.");
        }
        return parts;
    }

    private String createProviderName(String[] parts) {
        return parts[0] + "_" + parts[1];
    }

    private String getIdentifier(String clientRegistrationId, String memberId, OAuth2AuthorizedClient authorizedClient) {
        switch (clientRegistrationId) {
            case KAKAO_REGISTRATION_ID:
                return memberId;
            case GITHUB_REGISTRATION_ID:
                return new String(authorizedClient.getAccessTokenValue());
            default:
                throw new RuntimeException("Unsupported OAuth2 provider: " + clientRegistrationId);
        }
    }

    public void deleteMember(Member member) {
        memberRepository.delete(member);
    }
}
