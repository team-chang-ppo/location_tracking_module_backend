package org.changppo.account.response.exception.apikey;

import org.changppo.account.response.exception.common.ExceptionType;

public class GradeNotFoundException extends ApiKeyBusinessException{
    public GradeNotFoundException() {
        super(ExceptionType.GRADE_NOT_FOUND_EXCEPTION);
    }
}
