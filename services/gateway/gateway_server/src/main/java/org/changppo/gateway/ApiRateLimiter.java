package org.changppo.gateway;

import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

public interface ApiRateLimiter {

    Mono<Response> isAllowed(String routeId, String apiKey, Long requestedTokens);

    class Response {
        public static final Response API_KEY_NOT_FOUND = new Response(true, false, Collections.emptyMap());
        private final boolean apiKeyNotFound;
        private final boolean allowed;
        private final Map<String, String> headers;

        public Response(boolean apiKeyNotFound, boolean allowed, Map<String, String> headers) {
            this.apiKeyNotFound = apiKeyNotFound;
            this.allowed = allowed;
            Assert.notNull(headers, "headers may not be null");
            this.headers = headers;
        }

        public boolean isApiKeyNotFound() {
            return apiKeyNotFound;
        }

        public boolean isAllowed() {
            return allowed;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }


    }

}
