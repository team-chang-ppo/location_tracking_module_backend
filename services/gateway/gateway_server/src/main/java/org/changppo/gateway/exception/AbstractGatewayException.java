package org.changppo.gateway.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class AbstractGatewayException extends RuntimeException {
    private final ErrorCode errorCode;
}
