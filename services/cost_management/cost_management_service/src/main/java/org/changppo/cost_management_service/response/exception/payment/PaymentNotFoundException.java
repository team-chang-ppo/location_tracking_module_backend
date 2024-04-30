package org.changppo.cost_management_service.response.exception.payment;

import org.changppo.cost_management_service.response.exception.common.ExceptionType;
import org.changppo.cost_management_service.response.exception.member.MemberBusinessException;

public class PaymentNotFoundException extends MemberBusinessException {

    public PaymentNotFoundException() {
        super(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION);
    }
}
