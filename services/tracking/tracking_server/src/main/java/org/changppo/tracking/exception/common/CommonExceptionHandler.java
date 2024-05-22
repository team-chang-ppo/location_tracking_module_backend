package org.changppo.tracking.exception.common;

import lombok.extern.slf4j.Slf4j;
import org.changppo.commons.FailedResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.changppo.commons.FailedResponseBody.ErrorPayload;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<FailedResponseBody<ErrorPayload>> handleBusinessException(BusinessException e) {
        log.debug("BusinessException", e);
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(new FailedResponseBody<>(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<FailedResponseBody<ErrorPayload>> handleAccessDeniedException(AccessDeniedException e) {
        log.debug("handleAccessDeniedException", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new FailedResponseBody<>(ErrorCode.ACCESS_DENIED.getCode(), ErrorCode.ACCESS_DENIED.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<FailedResponseBody<ErrorPayload>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.debug("handleIllegalArgumentException", e);
        return ResponseEntity.badRequest()
                .body(new FailedResponseBody<>(ErrorCode.INVALID_INPUT_VALUE.getCode(), ErrorCode.INVALID_INPUT_VALUE.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FailedResponseBody<ErrorPayload>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.debug("MethodArgumentNotValidException", e);

        return ResponseEntity.badRequest()
                .body(new FailedResponseBody<>(ErrorCode.INVALID_INPUT_VALUE.getCode(), errorMessage));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<FailedResponseBody<ErrorPayload>> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        log.debug("MissingRequestHeaderException", e);

        return ResponseEntity.badRequest()
                .body(new FailedResponseBody<>(ErrorCode.HEADER_NOT_FOUND.getCode(), ErrorCode.HEADER_NOT_FOUND.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<FailedResponseBody<ErrorPayload>> handleException(Exception e) {
        log.debug("Exception", e);
        return  ResponseEntity.internalServerError()
                .body(new FailedResponseBody<>(ErrorCode.UNEXPECTED_SERVER_ERROR.getCode(), e.getMessage()));
    }
}
