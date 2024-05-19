package org.changppo.tracking.exception.common;

import lombok.extern.slf4j.Slf4j;
import org.changppo.utils.response.body.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response> handleBusinessException(BusinessException e) {
        log.debug("BusinessException", e);
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(Response.failure(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response> handleAccessDeniedException(AccessDeniedException e) {
        log.debug("handleAccessDeniedException", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Response.failure(ErrorCode.ACCESS_DENIED.getCode(), ErrorCode.ACCESS_DENIED.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response> handleIllegalArgumentException(IllegalArgumentException e) {
        log.debug("handleIllegalArgumentException", e);
        return ResponseEntity.badRequest()
                .body(Response.failure(ErrorCode.INVALID_INPUT_VALUE.getCode(), ErrorCode.INVALID_INPUT_VALUE.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.debug("MethodArgumentNotValidException", e);

        return ResponseEntity.badRequest()
                .body(Response.failure(ErrorCode.INVALID_INPUT_VALUE.getCode(), errorMessage));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Response> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        log.debug("MissingRequestHeaderException", e);

        return ResponseEntity.badRequest()
                .body(Response.failure(ErrorCode.HEADER_NOT_FOUND.getCode(), ErrorCode.HEADER_NOT_FOUND.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception e) {
        log.debug("Exception", e);
        return  ResponseEntity.internalServerError()
                .body(Response.failure(ErrorCode.UNEXPECTED_SERVER_ERROR.getCode(), e.getMessage()));
    }
}
