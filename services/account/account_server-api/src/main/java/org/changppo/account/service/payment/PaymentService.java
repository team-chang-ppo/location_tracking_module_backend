package org.changppo.account.service.payment;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.changppo.account.dto.payment.PaymentListDto;
import org.changppo.account.dto.payment.PaymentReadAllRequest;
import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.entity.payment.PaymentCardInfo;
import org.changppo.account.oauth2.OAuth2Client;
import org.changppo.account.payment.PaymentExecutionJobClient;
import org.changppo.account.payment.dto.PaymentExecutionJobRequest;
import org.changppo.account.payment.dto.PaymentExecutionJobResponse;
import org.changppo.account.paymentgateway.PaymentGatewayClient;
import org.changppo.account.repository.apikey.ApiKeyRepository;
import org.changppo.account.repository.card.CardRepository;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.repository.payment.PaymentRepository;
import org.changppo.account.response.exception.card.UnsupportedPaymentGatewayException;
import org.changppo.account.response.exception.member.UnsupportedOAuth2Exception;
import org.changppo.account.response.exception.payment.PaymentExecutionFailureException;
import org.changppo.account.response.exception.payment.PaymentNotFoundException;
import org.changppo.account.service.dto.payment.PaymentDto;
import org.changppo.account.service.event.payment.PaymentEventPublisher;
import org.changppo.account.type.PaymentGatewayType;
import org.changppo.account.type.PaymentStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;
    private final PaymentExecutionJobClient paymentExecutionJobClient;
    private final MemberRepository memberRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final CardRepository cardRepository;
    private final List<OAuth2Client> oauth2Clients;
    private final List<PaymentGatewayClient> paymentGatewayClients;

    @Transactional
    @PreAuthorize("@paymentAccessEvaluator.check(#id) and !@memberPaymentFailureStatusEvaluator.check(#id)")
    public PaymentDto repayment(@Param("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        Payment payment = paymentRepository.findById(id).orElseThrow(PaymentNotFoundException::new);
        PaymentExecutionJobResponse paymentExecutionJobResponse = paymentExecutionJobClient.PaymentExecutionJob(createPaymentExecutionJobRequest(payment)).getData().orElseThrow(PaymentExecutionFailureException::new);
        updatePaymentStatus(payment, paymentExecutionJobResponse);
        handlePaymentComplete(payment.getMember());
        if (payment.getMember().isDeletionRequested()){
            handleMemberDeletion(payment.getMember(), request, response);
        }
        paymentEventPublisher.publishEvent(payment);
        return new PaymentDto(payment.getId(), payment.getAmount(), payment.getStatus(), payment.getStartedAt(), payment.getEndedAt(), payment.getCardInfo(), payment.getCreatedAt());
    }

    private PaymentExecutionJobRequest createPaymentExecutionJobRequest(Payment payment) {
        return new PaymentExecutionJobRequest(
                payment.getMember().getId(),
                payment.getAmount(),
                payment.getEndedAt()
        );
    }

    private void updatePaymentStatus(Payment payment, PaymentExecutionJobResponse paymentExecutionJobResponse) {
        payment.changeStatus(PaymentStatus.COMPLETED_PAID, new PaymentCardInfo(paymentExecutionJobResponse.getType(), paymentExecutionJobResponse.getIssuerCorporation(), paymentExecutionJobResponse.getBin()));
    }

    public void handlePaymentComplete(Member member) {
        member.unbanForPaymentFailure();
        apiKeyRepository.unbanApiKeysForPaymentFailure(member.getId());
    }

    public void handleMemberDeletion(Member member, HttpServletRequest request, HttpServletResponse response) {
        deleteMemberApiKeys(member.getId());
        inactivePaymentGatewayCards(member.getId());
        deleteMemberCards(member.getId());
        deleteMemberPayment(member.getId());
        unlinkOAuth2Member(member.getName());
        memberRepository.delete(member);
        deleteSession(request);
        deleteCookie(response);
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


    public void deleteSession(HttpServletRequest request){
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public void deleteCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        // refreshCookie.setSecure(true);
        response.addCookie(cookie);
    }

    @PreAuthorize("@paymentAccessEvaluator.check(#memberId)")
    public PaymentListDto readAll(@Param("memberId")Long memberId, PaymentReadAllRequest req){
        Slice<PaymentDto> slice = paymentRepository.findAllByMemberIdOrderByDesc(memberId, req.getLastPaymentId(), Pageable.ofSize(req.getSize()));
        return new PaymentListDto(slice.getNumberOfElements(), slice.hasNext(), slice.getContent());
    }
}
