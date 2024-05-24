package org.changppo.monioring.server.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.commons.FailedResponseBody;
import org.changppo.monioring.server.exception.ErrorCode;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class SendErrorUtil {
    // objectMapper에서 발생한 예외를 처리하기 위해
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String INTERNAL_SERVER_ERROR_JSON;
    static {
        FailedResponseBody<?> failedResponseBody = ErrorCode.INTERNAL_SERVER_ERROR.toFailedResponseBody();
        try {
            INTERNAL_SERVER_ERROR_JSON = objectMapper.writeValueAsString(failedResponseBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }



    public static Mono<Void> sendError(ServerWebExchange exchange, ErrorCode errorCode) {
        return Mono.defer(() -> {
            log.debug("Send error: {}", errorCode);
            FailedResponseBody<?> failedResponseBody = errorCode.toFailedResponseBody();
            DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            try {
                DataBuffer dataBuffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(failedResponseBody));
                return response.writeWith(Mono.just(dataBuffer));
            } catch (JsonProcessingException e) {
                log.error("Failed to write error response", e);
                DataBuffer dataBuffer = bufferFactory.wrap(INTERNAL_SERVER_ERROR_JSON.getBytes());
                return response.writeWith(Mono.just(dataBuffer));
            }
        });
    }



}
