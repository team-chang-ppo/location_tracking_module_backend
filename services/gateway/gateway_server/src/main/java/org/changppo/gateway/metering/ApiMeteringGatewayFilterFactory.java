package org.changppo.gateway.metering;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.changppo.gateway.GatewayConstant;
import org.changppo.gateway.apikey.ApiKey;
import org.changppo.gateway.exception.ErrorCode;
import org.changppo.gateway.exception.ErrorResponse;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.HasRouteId;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;


@Slf4j
public class ApiMeteringGatewayFilterFactory extends AbstractGatewayFilterFactory<ApiMeteringGatewayFilterFactory.Config> {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public ApiMeteringGatewayFilterFactory(ReactiveRedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        super(Config.class);
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            Object attribute = exchange.getAttribute(ApiKey.class.getName());
            if (attribute == null) {
                // api key 가 없는 경우
                return responseApiKeyNotFoundError(exchange);
            }
            if (!(attribute instanceof ApiKey)) {
                throw new IllegalStateException("ApiKey is not found in ServerWebExchange");
            }
            var apiKeyContext = (ApiKey) attribute;

            // redis stream 을 통해 호출 정보를 발생시킨다
            String routeId = config.getRouteId();
            Long apiKeyId = apiKeyContext.id();
            ApiRecord apiRecord = new ApiRecord(apiKeyId, routeId, Instant.now());

            String recordJson;
            try {
                recordJson = objectMapper.writeValueAsString(apiRecord);
            } catch (Exception e) {
                log.error("Failed to serialize ApiRecord", e);
                throw new IllegalStateException("Failed to serialize ApiRecord");
            }

            return redisTemplate.opsForList().leftPush(GatewayConstant.API_METERING_QUEUE_KEY, recordJson)
                    .then(chain.filter(exchange));
        };
    }

    protected static Mono<Void> responseApiKeyNotFoundError(ServerWebExchange exchange) {
        // 에러 응답을 반환한다.
        ErrorCode errorCode = ErrorCode.API_KEY_NOT_FOUND;
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        exchange.getResponse().setStatusCode(errorCode.getHttpStatus());
        Mono<DataBuffer> bufferMono = Mono.just(exchange.getResponse().bufferFactory().wrap(errorResponse.toJsonString().getBytes()));
        return exchange.getResponse().writeWith(bufferMono);
    }

    @Getter @Setter
    public static class Config implements HasRouteId {
        private String routeId;
    }
}
