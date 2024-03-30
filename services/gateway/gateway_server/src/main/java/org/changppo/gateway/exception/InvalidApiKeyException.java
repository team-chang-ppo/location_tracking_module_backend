package org.changppo.gateway.exception;

public class InvalidApiKeyException extends AbstractGatewayException{

    public InvalidApiKeyException() {
        super(ErrorCode.INVALID_API_KEY);
    }
}
