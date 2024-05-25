package org.changppo.monitoring.exception;

import lombok.extern.slf4j.Slf4j;
import org.changppo.commons.FailedResponseBody;
import org.changppo.monioring.domain.error.AbstractMonitoringServerException;
import org.changppo.monioring.domain.error.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<FailedResponseBody<?>> handleAccessDeniedException(AccessDeniedException e) {
        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
        log.debug("AccessDeniedException", e);
        return ResponseEntity.status(errorCode.getResponseStatus()).body(errorCode.toResponse());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<FailedResponseBody<?>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.debug("MissingServletRequestParameterException", e);
        return ResponseEntity.badRequest().body(ErrorCode.INVALID_INPUT_VALUE.toResponse());
    }

    @ExceptionHandler(AbstractMonitoringServerException.class)
    public ResponseEntity<FailedResponseBody<?>> handleMonitoringServerException(AbstractMonitoringServerException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.debug("MonitoringServerException", e);
        return ResponseEntity.status(errorCode.getResponseStatus()).body(errorCode.toResponse());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<FailedResponseBody<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.debug("IllegalArgumentException", e);
        return ResponseEntity.badRequest().body(ErrorCode.INVALID_INPUT_VALUE.toResponse());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<FailedResponseBody<?>> handleIllegalStateException(IllegalStateException e) {
        log.error("IllegalStateException", e);
        return ResponseEntity.internalServerError().body(ErrorCode.INTERNAL_SERVER_ERROR.toResponse());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<FailedResponseBody<?>> handleException(Exception e) {
        log.error("Unknown Exception", e);
        return ResponseEntity.internalServerError().body(ErrorCode.INTERNAL_SERVER_ERROR.toResponse());
    }

}
