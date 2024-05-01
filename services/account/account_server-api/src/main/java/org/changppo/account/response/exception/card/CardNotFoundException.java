package org.changppo.account.response.exception.card;

import org.changppo.account.response.exception.common.ExceptionType;

public class CardNotFoundException extends CardBusinessException {
    public CardNotFoundException() {
        super(ExceptionType.CARD_NOT_FOUND_EXCEPTION);
    }
}
