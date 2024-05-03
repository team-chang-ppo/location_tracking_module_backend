package org.changppo.account.response.exception.payment;

import org.changppo.account.response.exception.common.ExceptionType;
import org.changppo.account.response.exception.member.MemberBusinessException;

public class PaymentNotFoundException extends MemberBusinessException {

    public PaymentNotFoundException() {
        super(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION);
    }
}
