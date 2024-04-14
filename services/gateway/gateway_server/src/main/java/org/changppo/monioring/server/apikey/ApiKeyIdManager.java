package org.changppo.monioring.server.apikey;

import reactor.core.publisher.Mono;

public interface ApiKeyIdManager {
    Mono<Boolean> isValidApiKeyId(Long id);

}
