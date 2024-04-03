package org.changppo.cost_management_service.response.exception.apikey;

import org.changppo.cost_management_service.response.exception.common.ExceptionType;

public class GradeNotFoundException extends ApiKeyBusinessException{
    public GradeNotFoundException() {
        super(ExceptionType.GRADE_NOT_FOUND_EXCEPTION);
    }
}
