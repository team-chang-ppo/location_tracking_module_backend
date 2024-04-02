package org.changppo.cost_management_service.exception.card;

import org.changppo.cost_management_service.exception.common.ExceptionType;

public class CardRegisterFailureException extends CardBusinessException {

    public CardRegisterFailureException() {
        super(ExceptionType.CARD_REGISTER_FAILURE_EXCEPTION);
    }

    public CardRegisterFailureException(Throwable cause) {
        super(ExceptionType.CARD_REGISTER_FAILURE_EXCEPTION, cause);
    }
}