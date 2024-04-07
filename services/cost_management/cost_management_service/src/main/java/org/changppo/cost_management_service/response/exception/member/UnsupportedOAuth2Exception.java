package org.changppo.cost_management_service.response.exception.member;

import org.changppo.cost_management_service.response.exception.common.ExceptionType;

public class UnsupportedOAuth2Exception extends MemberBusinessException {
    public UnsupportedOAuth2Exception() {
        super(ExceptionType.UNSUPPORTED_OAUTH2_EXCEPTION);
    }
}