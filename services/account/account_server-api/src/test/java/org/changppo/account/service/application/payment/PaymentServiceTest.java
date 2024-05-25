package org.changppo.account.service.application.payment;

import org.changppo.account.dto.payment.PaymentListDto;
import org.changppo.account.dto.payment.PaymentReadAllRequest;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.service.domain.apikey.ApiKeyDomainService;
import org.changppo.account.service.domain.member.MemberDomainService;
import org.changppo.account.service.domain.payment.PaymentDomainService;
import org.changppo.account.service.dto.payment.PaymentDto;
import org.changppo.account.service.event.payment.PaymentEventPublisher;
import org.changppo.account.type.PaymentStatus;
import org.changppo.account.type.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.changppo.account.builder.member.MemberBuilder.buildMember;
import static org.changppo.account.builder.member.RoleBuilder.buildRole;
import static org.changppo.account.builder.pageable.PageableBuilder.buildPage;
import static org.changppo.account.builder.payment.PaymentBuilder.buildPayment;
import static org.changppo.account.builder.payment.PaymentDtoBuilder.buildPaymentDto;
import static org.changppo.account.builder.payment.PaymentRequestBuilder.buildPaymentReadAllRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;
    @Mock
    private PaymentDomainService paymentDomainService;
    @Mock
    private PaymentEventPublisher paymentEventPublisher;
    @Mock
    private MemberDomainService memberDomainService;
    @Mock
    private ApiKeyDomainService apiKeyDomainService;

    Role role;
    Member member;

    @BeforeEach
    void beforeEach() {
        role = buildRole(RoleType.ROLE_NORMAL);
        member = buildMember(role);
    }

    @Test
    void repaymentTest() {
        // given
        Payment payment = buildPayment(member);
        given(paymentDomainService.processRepayment(anyLong())).willReturn(payment);
        doNothing().when(memberDomainService).unbanMemberPaymentFailure(any(Member.class));
        doNothing().when(apiKeyDomainService).unbanApiKeysForPaymentFailure(payment.getMember().getId());
        doNothing().when(paymentEventPublisher).publishEvent(any(Payment.class));

        // when
        PaymentDto result = paymentService.repayment(1L);

        // then
        assertThat(result.getId()).isEqualTo(payment.getId());
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.COMPLETED_PAID);
    }

    @Test
    void readAllTest() {
        // given
        PaymentReadAllRequest request = buildPaymentReadAllRequest(1L, 10);
        List<PaymentDto> paymentDtos = List.of(buildPaymentDto(), buildPaymentDto());
        PaymentListDto paymentListDto = new PaymentListDto(paymentDtos.size(), true, paymentDtos);
        given(paymentDomainService.getPaymentList(anyLong(), anyLong(), any(Pageable.class))).willReturn(paymentListDto);

        // when
        PaymentListDto result = paymentService.readAll(1L, request);

        // then
        assertThat(result.getPaymentList()).isEqualTo(paymentDtos);
    }

    @Test
    void readListTest() {
        // given
        Pageable pageable = buildPage();
        List<PaymentDto> paymentDtos = List.of(buildPaymentDto(), buildPaymentDto());
        Page<PaymentDto> page = new PageImpl<>(paymentDtos, pageable, paymentDtos.size());
        given(paymentDomainService.getPaymentDtos(any(Pageable.class))).willReturn(page);

        // when
        Page<PaymentDto> result = paymentService.readList(pageable);

        // then
        assertThat(result.getContent()).isEqualTo(paymentDtos);
    }
}
