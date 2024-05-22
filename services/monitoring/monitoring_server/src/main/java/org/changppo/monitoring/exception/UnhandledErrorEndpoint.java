package org.changppo.monitoring.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.changppo.commons.FailedResponseBody;
import org.changppo.monioring.domain.error.ErrorCode;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping({"${server.error.path:${error.path:/error}}"})
public class UnhandledErrorEndpoint extends AbstractErrorController {
    private final static ErrorAttributeOptions ERROR_ATTRIBUTE_OPTIONS;
    private final static String ERROR_CODE = ErrorCode.INTERNAL_SERVER_ERROR.getCode();

    static {
        ERROR_ATTRIBUTE_OPTIONS = ErrorAttributeOptions.defaults();
        ERROR_ATTRIBUTE_OPTIONS.including(ErrorAttributeOptions.Include.MESSAGE);
        ERROR_ATTRIBUTE_OPTIONS.including(ErrorAttributeOptions.Include.EXCEPTION);
    }


    public UnhandledErrorEndpoint(ErrorAttributes errorAttributes, List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorViewResolvers);

    }

    @RequestMapping
    public ResponseEntity<FailedResponseBody<?>> error(HttpServletRequest request) {
        log.error("Unhandled error occurred");
        Map<String, Object> errorAttributes = this.getErrorAttributes(request, ERROR_ATTRIBUTE_OPTIONS);
        String message = errorAttributes.getOrDefault("message","").toString();
        String exception = errorAttributes.getOrDefault("exception","").toString();
        log.error("Unhandled Error Occurred and forwarded to {} with message: {} and exception: {}", request.getRequestURI(), message, exception);
        String responseMessage = "Unexpected Server Error, message: %s, exception: %s".formatted(message, exception);

        FailedResponseBody<?> responseBody = new FailedResponseBody<>(ERROR_CODE, responseMessage);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(responseBody);
    }

    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    public ResponseEntity<FailedResponseBody<?>> mediaTypeNotAcceptable(HttpServletRequest request) {
        HttpStatus status = this.getStatus(request);
        FailedResponseBody<?> body = ErrorCode.HTTP_MEDIA_TYPE_NOT_ACCEPTABLE.toResponse();
        return ResponseEntity.status(status).body(body);
    }
}
