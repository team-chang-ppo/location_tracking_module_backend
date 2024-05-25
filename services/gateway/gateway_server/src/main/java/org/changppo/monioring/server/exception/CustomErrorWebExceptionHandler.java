package org.changppo.monioring.server.exception;

import org.changppo.commons.FailedResponseBody;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

public class CustomErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    private final static ErrorAttributeOptions DEFAULT_ERROR_ATTRIBUTE_OPTIONS;
    static {
        DEFAULT_ERROR_ATTRIBUTE_OPTIONS = ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.EXCEPTION,
                ErrorAttributeOptions.Include.MESSAGE,
                ErrorAttributeOptions.Include.STACK_TRACE,
                ErrorAttributeOptions.Include.BINDING_ERRORS
        );
    }
    /**
     * Create a new {@code AbstractErrorWebExceptionHandler}.
     *
     * @param errorAttributes    the error attributes
     * @param resources          the resources configuration properties
     * @param applicationContext the application context
     * @since 2.4.0
     */
    public CustomErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources, ApplicationContext applicationContext) {
        super(errorAttributes, resources, applicationContext);
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(all(), this::renderErrorResponse);
    }

    protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Map<String, Object> error = getErrorAttributes(request, DEFAULT_ERROR_ATTRIBUTE_OPTIONS);
        String message = (String) error.get("message");
        Integer status = (Integer) error.get("status");
        if (status == null) {
            status = 500;
        }
        String errorCode = ErrorCode.UNHANDLED_ERROR.getCode();
        FailedResponseBody<?> failedResponseBody = new FailedResponseBody<>(errorCode, message);

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(failedResponseBody);
    }

}
