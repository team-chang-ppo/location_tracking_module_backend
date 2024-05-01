package org.changppo.account.security.evaluator;

import lombok.RequiredArgsConstructor;
import org.changppo.account.type.RoleType;
import org.changppo.account.payment.Payment;
import org.changppo.account.repository.payment.PaymentRepository;
import org.changppo.account.response.exception.payment.PaymentNotFoundException;
import org.changppo.account.security.PrincipalHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentAccessEvaluator extends Evaluator {

    private final PaymentRepository paymentRepository;
    private static final List<RoleType> roleTypes = List.of(RoleType.ROLE_ADMIN);

    @Override
    protected List<RoleType> getRoleTypes() {
        return roleTypes;
    }

    @Override
    protected boolean isEligible(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(PaymentNotFoundException::new);
        return payment.getMember().getId().equals(PrincipalHandler.extractId());
    }
}