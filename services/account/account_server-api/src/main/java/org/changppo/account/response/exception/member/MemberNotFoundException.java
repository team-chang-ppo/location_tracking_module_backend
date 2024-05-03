package org.changppo.account.response.exception.member;

import org.changppo.account.response.exception.common.ExceptionType;

public class MemberNotFoundException extends MemberBusinessException {

    public MemberNotFoundException() {
        super(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION);
    }
}
