package org.changppo.gateway.metering;

import reactor.core.publisher.Mono;

public interface ApiMeteringEventPublisher {

    /**
     * Publishes the given {@link ApiMeteringEvent} to the event bus.
     *
     * @param apiMeteringEvent the event to publish
     * @return a {@link Mono} that completes when the event has been published, {@link Mono#error(Throwable)} if an error occurs
     */
    Mono<Void> publish(ApiMeteringEvent apiMeteringEvent);
}
