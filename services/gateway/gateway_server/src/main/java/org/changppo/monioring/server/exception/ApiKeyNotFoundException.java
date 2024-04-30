package org.changppo.monioring.server.exception;

public class ApiKeyNotFoundException extends AbstractGatewayException{
    public ApiKeyNotFoundException() {
        super(ErrorCode.API_KEY_NOT_FOUND);
    }
}
