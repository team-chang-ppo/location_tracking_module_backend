package org.changppo.account.service.domain.payment;

import org.changppo.account.dto.payment.PaymentListDto;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.payment.PaymentExecutionJobClient;
import org.changppo.account.payment.dto.PaymentExecutionJobRequest;
import org.changppo.account.payment.dto.PaymentExecutionJobResponse;
import org.changppo.account.repository.payment.PaymentRepository;
import org.changppo.account.response.ClientResponse;
import org.changppo.account.response.exception.payment.PaymentExecutionFailureException;
import org.changppo.account.response.exception.payment.PaymentNotFoundException;
import org.changppo.account.service.dto.payment.PaymentDto;
import org.changppo.account.type.PaymentStatus;
import org.changppo.account.type.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.changppo.account.builder.member.MemberBuilder.buildMember;
import static org.changppo.account.builder.member.RoleBuilder.buildRole;
import static org.changppo.account.builder.pageable.PageableBuilder.buildPage;
import static org.changppo.account.builder.payment.PaymentBuilder.buildPayment;
import static org.changppo.account.builder.payment.PaymentDtoBuilder.buildPaymentDto;
import static org.changppo.account.builder.payment.PaymentExecutionJobResponseBuilder.buildPaymentExecutionJobResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PaymentDomainServiceTest {

    @InjectMocks
    PaymentDomainService paymentDomainService;

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    PaymentExecutionJobClient paymentExecutionJobClient;

    Member member;
    Role role;
    Payment payment;
    PaymentExecutionJobResponse paymentExecutionJobResponse;

    @BeforeEach
    void beforeEach() {
        role = buildRole(RoleType.ROLE_NORMAL);
        member = buildMember(role);
        payment = buildPayment(member);
        paymentExecutionJobResponse = buildPaymentExecutionJobResponse();
    }

    @Test
    void processRepaymentTest() {
        // given
        given(paymentRepository.findById(anyLong())).willReturn(Optional.of(payment));
        given(paymentExecutionJobClient.PaymentExecutionJob(any(PaymentExecutionJobRequest.class))).willReturn(ClientResponse.success(paymentExecutionJobResponse));

        // when
        Payment result = paymentDomainService.processRepayment(1L);

        // then
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.COMPLETED_PAID);
        assertThat(result.getKey()).isEqualTo(paymentExecutionJobResponse.getKey());
        assertThat(result.getCardInfo().getType()).isEqualTo(paymentExecutionJobResponse.getCardType());
        assertThat(result.getCardInfo().getIssuerCorporation()).isEqualTo(paymentExecutionJobResponse.getCardIssuerCorporation());
        assertThat(result.getCardInfo().getBin()).isEqualTo(paymentExecutionJobResponse.getCardBin());
    }

    @Test
    void processRepaymentExceptionTest() {
        // given
        given(paymentRepository.findById(anyLong())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> paymentDomainService.processRepayment(1L)).isInstanceOf(PaymentNotFoundException.class);
    }

    @Test
    void processRepaymentExecutionFailureTest() {
        // given
        given(paymentRepository.findById(anyLong())).willReturn(Optional.of(payment));
        given(paymentExecutionJobClient.PaymentExecutionJob(any(PaymentExecutionJobRequest.class))).willReturn(ClientResponse.failure());

        // when, then
        assertThatThrownBy(() -> paymentDomainService.processRepayment(1L)).isInstanceOf(PaymentExecutionFailureException.class);
    }

    @Test
    void getPaymentListTest() {
        // given
        Pageable pageable = buildPage();
        List<PaymentDto> paymentDtos = List.of(buildPaymentDto(), buildPaymentDto());
        Slice<PaymentDto> slice = new SliceImpl<>(paymentDtos, pageable, false);
        given(paymentRepository.findAllDtosByMemberIdAndStatusNotCompletedFree(anyLong(), anyLong(), any(Pageable.class))).willReturn(slice);

        // when
        PaymentListDto result = paymentDomainService.getPaymentList(1L, 1L, pageable);

        // then
        assertThat(result.getPaymentList()).isEqualTo(paymentDtos);
    }

    @Test
    void getPaymentDtosTest() {
        // given
        Pageable pageable = buildPage();
        List<PaymentDto> paymentDtos = List.of(buildPaymentDto(), buildPaymentDto());
        Page<PaymentDto> paymentDtoPage = new PageImpl<>(paymentDtos, pageable, paymentDtos.size());
        given(paymentRepository.findAllDtos(any(Pageable.class))).willReturn(paymentDtoPage);

        // when
        Page<PaymentDto> result = paymentDomainService.getPaymentDtos(pageable);

        // then
        assertThat(result.getContent()).isEqualTo(paymentDtos);
    }
}
