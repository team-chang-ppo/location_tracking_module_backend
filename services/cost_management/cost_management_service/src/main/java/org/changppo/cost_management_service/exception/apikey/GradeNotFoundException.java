package org.changppo.cost_management_service.exception.apikey;

import org.changppo.cost_management_service.exception.common.ExceptionType;

public class GradeNotFoundException extends ApiKeyBusinessException{
    public GradeNotFoundException() {
        super(ExceptionType.GRADE_NOT_FOUND_EXCEPTION);
    }
}
