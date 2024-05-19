package org.changppo.monitoring.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.changppo.monioring.domain.error.ErrorCode;
import org.changppo.monioring.domain.response.Response;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping({"${server.error.path:${error.path:/error}}"})
public class UnhandledErrorEndpoint extends AbstractErrorController {


    public UnhandledErrorEndpoint(ErrorAttributes errorAttributes, List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorViewResolvers);

    }

    @RequestMapping
    public ResponseEntity<Response> error(HttpServletRequest request) {
        log.error("Unhandled error occurred");
        HttpStatus status = this.getStatus(request);
        if (status == HttpStatus.NO_CONTENT) {
            return new ResponseEntity(status);
        } else {
            Response body = ErrorCode.INTERNAL_SERVER_ERROR.toResponse();
            return new ResponseEntity(body, status);
        }
    }

    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    public ResponseEntity<Response> mediaTypeNotAcceptable(HttpServletRequest request) {
        HttpStatus status = this.getStatus(request);
        Response body = ErrorCode.HTTP_MEDIA_TYPE_NOT_ACCEPTABLE.toResponse();
        return ResponseEntity.status(status).body(body);
    }
}
