package org.changppo.cost_management_service.exception.card;

import org.changppo.cost_management_service.exception.common.ExceptionType;

public class CardNotFoundException extends CardBusinessException {
    public CardNotFoundException() {
        super(ExceptionType.CARD_NOT_FOUND_EXCEPTION);
    }
}
