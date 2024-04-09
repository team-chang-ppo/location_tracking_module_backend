package org.changppo.cost_management_service.response.exception.common;

import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException{

    private final ExceptionType exceptionType;

    public BusinessException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

    public BusinessException(ExceptionType exceptionType, Throwable cause) {
        super(exceptionType.getMessage(), cause);
        this.exceptionType = exceptionType;
    }
}