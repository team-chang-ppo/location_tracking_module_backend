package org.changppo.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.account.entity.apikey.ApiKey;
import org.changppo.account.entity.apikey.Grade;
import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.card.PaymentGateway;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;
import org.changppo.account.entity.member.oauth2.OAuth2AuthorizedClient;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.entity.payment.PaymentCardInfo;
import org.changppo.account.repository.apikey.ApiKeyRepository;
import org.changppo.account.repository.apikey.GradeRepository;
import org.changppo.account.repository.card.CardRepository;
import org.changppo.account.repository.card.PaymentGatewayRepository;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.repository.member.RoleRepository;
import org.changppo.account.repository.member.oauth2.OAuth2AuthorizedClientRepository;
import org.changppo.account.repository.payment.PaymentRepository;
import org.changppo.account.type.GradeType;
import org.changppo.account.type.PaymentGatewayType;
import org.changppo.account.type.PaymentStatus;
import org.changppo.account.type.RoleType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.changppo.account.batch.config.database.TransactionManagerConfig.DOMAIN_TRANSACTION_MANAGER;

@Profile("test")
@Component
@RequiredArgsConstructor
@Slf4j
public class TestInitDB {

    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final OAuth2AuthorizedClientRepository authorizedClientRepository;
    private final GradeRepository gradeRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final PaymentGatewayRepository paymentGatewayRepository;
    private final CardRepository cardRepository;
    private final PaymentRepository paymentRepository;

    @Getter
    private final String freeMemberName = "OAUTH2_KAKAO_1234";
    @Getter
    private final String normalMemberName = "OAUTH2_KAKAO_2345";
    @Getter
    private final String banForPaymentFailureMemberName = "OAUTH2_KAKAO_3456";
    @Getter
    private final String requestDeletionMemberName = "OAUTH2_KAKAO_4567";
    @Getter
    private final String freeApiKeyValue = "free-api-key";
    @Getter
    private final String classicApiKeyValue = "classic-api-key";
    @Getter
    private final String banForPaymentFailureApiKeyValue = "ban-payment-fail-api-key";
    @Getter
    private final String banForCardDeletionApiKeyValue = "ban-card-delete-api-key";
    @Getter
    private final String requestDeletionApiKeyValue = "request-delete-api-key";
    @Getter
    private final String kakaopayCardByNormalMemberKey = "kakaopay-NormalMember-card-key";
    @Getter
    private final String kakaopayCardByRequestDeletionMemberKey = "kakaopay-RequestDeletionMember-card-key";
    @Getter
    private final String kakaopayCardByBanForPaymentFailureMemberKey = "kakaopay-BanForPaymentFailureMember-card-key";
    @Getter
    private final String successfulFreePaymentKey = "successful-free-payment-key";
    @Getter
    private final String successfulPaidPaymentKey = "successful-paid-payment-key";
    @Getter
    private final String failedPaymentKey = "failed-payment-key";

    @Transactional(transactionManager = DOMAIN_TRANSACTION_MANAGER)
    public void initMember() {
        initRole();
        initTestMember();
        initTestOAuth2AuthorizedClient();
    }

    @Transactional(transactionManager = DOMAIN_TRANSACTION_MANAGER)
    public void initApiKey() {
        initGrade();
        initTestApiKey();
    }

    @Transactional(transactionManager = DOMAIN_TRANSACTION_MANAGER)
    public void initCard() {
        initPaymentGateway();
        initTestCard();
    }

    @Transactional(transactionManager = DOMAIN_TRANSACTION_MANAGER)
    public void initPayment() {
        initTestPayment();
    }

    private void initRole() {
        roleRepository.saveAll(
                Stream.of(RoleType.values()).map(Role::new).collect(Collectors.toList())
        );
    }

    private void initTestMember() {
        Role freeRole = roleRepository.findByRoleType(RoleType.ROLE_FREE).orElseThrow();
        Role normalRole = roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow();

        Member freeMember = Member.builder()
                .name(freeMemberName)
                .username("free")
                .profileImage("freeMemberProfileImage")
                .role(freeRole)
                .build();
        Member normalMember = Member.builder()
                .name(normalMemberName)
                .username("normal")
                .profileImage("normalMemberProfileImage")
                .role(normalRole)
                .build();
        Member banForPaymentFailureMember = Member.builder()
                .name(banForPaymentFailureMemberName)
                .username("banForPaymentFailureMember")
                .profileImage("banForPaymentFailureMemberProfileImage")
                .role(normalRole)
                .build();
        banForPaymentFailureMember.banForPaymentFailure(LocalDateTime.now());
        Member requestDeletionMember = Member.builder()
                .name(requestDeletionMemberName)
                .username("requestDeletionMember")
                .profileImage("requestDeletionMemberProfileImage")
                .role(normalRole)
                .build();
        requestDeletionMember.requestDeletion(LocalDateTime.now().plusHours(1));
        memberRepository.saveAll(List.of(freeMember, normalMember, banForPaymentFailureMember, requestDeletionMember));
    }

    private void initTestOAuth2AuthorizedClient() {
        Member freeMember = memberRepository.findByName(freeMemberName).orElseThrow();
        Member normalMember = memberRepository.findByName(normalMemberName).orElseThrow();
        Member banForPaymentFailureMember = memberRepository.findByName(banForPaymentFailureMemberName).orElseThrow();
        Member requestDeletionMember = memberRepository.findByName(requestDeletionMemberName).orElseThrow();

        OAuth2AuthorizedClient freeOAuth2AuthorizedClient = OAuth2AuthorizedClient.builder()
                .principalName(freeMember.getName())
                .clientRegistrationId("kakao")
                .accessTokenType("Bearer")
                .accessTokenValue("free-access-token".getBytes())
                .accessTokenIssuedAt(LocalDateTime.now().minusDays(9))
                .accessTokenExpiresAt(LocalDateTime.now().plusDays(1))
                .accessTokenScopes("read_profile")
                .refreshTokenValue("free-refresh-token".getBytes())
                .refreshTokenIssuedAt(LocalDateTime.now().minusDays(8))
                .build();

        OAuth2AuthorizedClient normalOAuth2AuthorizedClient = OAuth2AuthorizedClient.builder()
                .principalName(normalMember.getName())
                .clientRegistrationId("kakao")
                .accessTokenType("Bearer")
                .accessTokenValue("normal-access-token".getBytes())
                .accessTokenIssuedAt(LocalDateTime.now().minusDays(9))
                .accessTokenExpiresAt(LocalDateTime.now().plusDays(1))
                .accessTokenScopes("read_profile,write_profile")
                .refreshTokenValue("normal-refresh-token".getBytes())
                .refreshTokenIssuedAt(LocalDateTime.now().minusDays(8))
                .build();

        OAuth2AuthorizedClient banForPaymentFailureOAuth2AuthorizedClient = OAuth2AuthorizedClient.builder()
                .principalName(banForPaymentFailureMember.getName())
                .clientRegistrationId("kakao")
                .accessTokenType("Bearer")
                .accessTokenValue("ban-for-payment-failure-access-token".getBytes())
                .accessTokenIssuedAt(LocalDateTime.now().minusDays(9))
                .accessTokenExpiresAt(LocalDateTime.now().plusDays(1))
                .accessTokenScopes("read_profile")
                .refreshTokenValue("ban-for-payment-failure-refresh-token".getBytes())
                .refreshTokenIssuedAt(LocalDateTime.now().minusDays(8))
                .build();

        OAuth2AuthorizedClient requestDeletionOAuth2AuthorizedClient = OAuth2AuthorizedClient.builder()
                .principalName(requestDeletionMember.getName())
                .clientRegistrationId("kakao")
                .accessTokenType("Bearer")
                .accessTokenValue("request-deletion-access-token".getBytes())
                .accessTokenIssuedAt(LocalDateTime.now().minusDays(9))
                .accessTokenExpiresAt(LocalDateTime.now().plusDays(1))
                .accessTokenScopes("read_profile")
                .refreshTokenValue("request-deletion-refresh-token".getBytes())
                .refreshTokenIssuedAt(LocalDateTime.now().minusDays(8))
                .build();

        authorizedClientRepository.saveAll(List.of(freeOAuth2AuthorizedClient, normalOAuth2AuthorizedClient, banForPaymentFailureOAuth2AuthorizedClient, requestDeletionOAuth2AuthorizedClient));
    }

    private void initGrade() {
        gradeRepository.saveAll(
                Stream.of(GradeType.values()).map(Grade::new).collect(Collectors.toList())
        );
    }

    private void initTestApiKey() {
        Member freeMember = memberRepository.findByName(freeMemberName).orElseThrow();
        Member normalMember = memberRepository.findByName(normalMemberName).orElseThrow();
        Member banForPaymentFailureMember = memberRepository.findByName(banForPaymentFailureMemberName).orElseThrow();
        Member requestDeletionMember = memberRepository.findByName(requestDeletionMemberName).orElseThrow();
        Grade freeGrade = gradeRepository.findByGradeType(GradeType.GRADE_FREE).orElseThrow();
        Grade classicGrade = gradeRepository.findByGradeType(GradeType.GRADE_CLASSIC).orElseThrow();

        ApiKey freeApiKey = ApiKey.builder()
                .value(freeApiKeyValue)
                .grade(freeGrade)
                .member(freeMember)
                .build();
        ApiKey classicApiKey = ApiKey.builder()
                .value(classicApiKeyValue)
                .grade(classicGrade)
                .member(normalMember)
                .build();
        ApiKey banForPaymentFailureApiKey = ApiKey.builder()
                .value(banForPaymentFailureApiKeyValue)
                .grade(classicGrade)
                .member(banForPaymentFailureMember)
                .build();
        banForPaymentFailureApiKey.banForPaymentFailure(LocalDateTime.now());
        ApiKey banForCardDeletionApiKey = ApiKey.builder()
                .value(banForCardDeletionApiKeyValue)
                .grade(classicGrade)
                .member(banForPaymentFailureMember)
                .build();
        banForCardDeletionApiKey.banForCardDeletion(LocalDateTime.now());
        ApiKey requestDeletionApiKey = ApiKey.builder()
                .value(requestDeletionApiKeyValue)
                .grade(classicGrade)
                .member(requestDeletionMember)
                .build();
        requestDeletionApiKey.requestDeletion(LocalDateTime.now());
        apiKeyRepository.saveAll(List.of(freeApiKey, classicApiKey, banForPaymentFailureApiKey, banForCardDeletionApiKey, requestDeletionApiKey));
    }

    private void initPaymentGateway() {
        paymentGatewayRepository.saveAll(
                Stream.of(PaymentGatewayType.values()).map(PaymentGateway::new).collect(Collectors.toList())
        );
    }

    private void initTestCard() {
        Member normalMember = memberRepository.findByName(normalMemberName).orElseThrow();
        Member requestDeletionMember = memberRepository.findByName(requestDeletionMemberName).orElseThrow();
        Member banForPaymentFailureMember = memberRepository.findByName(banForPaymentFailureMemberName).orElseThrow();
        PaymentGateway kakaopayPaymentGateway = paymentGatewayRepository.findByPaymentGatewayType(PaymentGatewayType.PG_KAKAOPAY).orElseThrow();

        Card kakaopayCardByNormalMember = Card.builder()
                .key(kakaopayCardByNormalMemberKey)
                .member(normalMember)
                .paymentGateway(kakaopayPaymentGateway)
                .type("신용")
                .issuerCorporation("Test Bank")
                .acquirerCorporation("Test Acquirer")
                .bin("123456")
                .build();

        Card kakaopayCardByRequestDeletionMember = Card.builder()
                .key(kakaopayCardByRequestDeletionMemberKey)
                .member(requestDeletionMember)
                .paymentGateway(kakaopayPaymentGateway)
                .type("신용")
                .issuerCorporation("Test Bank")
                .acquirerCorporation("Test Acquirer")
                .bin("123456")
                .build();

        Card kakaopayCardByBanForPaymentFailureMember = Card.builder()
                .key(kakaopayCardByBanForPaymentFailureMemberKey)
                .member(banForPaymentFailureMember)
                .paymentGateway(kakaopayPaymentGateway)
                .type("신용")
                .issuerCorporation("Test Bank")
                .acquirerCorporation("Test Acquirer")
                .bin("123456")
                .build();

        cardRepository.saveAll(List.of(kakaopayCardByNormalMember, kakaopayCardByRequestDeletionMember, kakaopayCardByBanForPaymentFailureMember));
    }

    public void initTestPayment() {
        Member freeMember = memberRepository.findByName(freeMemberName).orElseThrow();
        Member banForPaymentFailureMember = memberRepository.findByName(banForPaymentFailureMemberName).orElseThrow();
        Card kakaopayCardByNormalMember = cardRepository.findByKey(kakaopayCardByNormalMemberKey).orElseThrow();

        Payment successfulfreePayment = Payment.builder()
                .key(successfulFreePaymentKey)
                .amount(new BigDecimal("0"))
                .status(PaymentStatus.COMPLETED_FREE)
                .startedAt(LocalDateTime.now().minusDays(9))
                .endedAt(LocalDateTime.now().minusDays(8))
                .member(freeMember)
                .build();

        Payment successfulPaidPayment = Payment.builder()
                .key(successfulPaidPaymentKey)
                .amount(new BigDecimal("200.00"))
                .status(PaymentStatus.COMPLETED_PAID)
                .startedAt(LocalDateTime.now().minusDays(9))
                .endedAt(LocalDateTime.now().minusDays(8))
                .member(kakaopayCardByNormalMember.getMember())
                .cardInfo(new PaymentCardInfo(kakaopayCardByNormalMember.getType(), kakaopayCardByNormalMember.getIssuerCorporation(), kakaopayCardByNormalMember.getBin()))
                .build();

        Payment failedPayment = Payment.builder()
                .key(failedPaymentKey)
                .amount(new BigDecimal("333.33"))
                .status(PaymentStatus.FAILED)
                .startedAt(LocalDateTime.now())
                .endedAt(LocalDateTime.now().plusHours(1))
                .member(banForPaymentFailureMember)
                .build();

        paymentRepository.saveAll(List.of(successfulfreePayment, successfulPaidPayment, failedPayment));
    }
}
