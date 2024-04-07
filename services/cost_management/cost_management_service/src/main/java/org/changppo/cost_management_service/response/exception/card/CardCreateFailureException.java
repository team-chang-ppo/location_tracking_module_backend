package org.changppo.cost_management_service.response.exception.card;

import org.changppo.cost_management_service.response.exception.common.ExceptionType;

public class CardCreateFailureException extends CardBusinessException {

    public CardCreateFailureException() {
        super(ExceptionType.CARD_CREATE_FAILURE_EXCEPTION);
    }

    public CardCreateFailureException(Throwable cause) {
        super(ExceptionType.CARD_CREATE_FAILURE_EXCEPTION, cause);
    }
}