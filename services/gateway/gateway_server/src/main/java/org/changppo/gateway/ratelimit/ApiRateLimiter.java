package org.changppo.gateway.ratelimit;

import org.changppo.gateway.ratelimit.context.ValidApiRateContext;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ApiRateLimiter {

    Mono<Response> isAllowed(String routeId, ValidApiRateContext apiRateContext, Long requestedTokens);

    class Response {
        private final boolean allowed;
        private final Map<String, String> headers;

        public Response(boolean allowed, Map<String, String> headers) {
            this.allowed = allowed;
            Assert.notNull(headers, "headers may not be null");
            this.headers = headers;
        }

        public boolean isAllowed() {
            return allowed;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }


    }

}
