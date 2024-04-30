package org.changppo.monioring.server.metering;

import org.changppo.monioring.domain.ApiMeteringEventPayLoad;
import reactor.core.publisher.Mono;

public interface ApiMeteringEventPublisher {

    /**
     * Publishes the given {@link ApiMeteringEventPayLoad} to the event bus.
     *
     * @param apiMeteringEventPayLoad the event to publish
     * @return a {@link Mono} that completes when the event has been published, {@link Mono#error(Throwable)} if an error occurs
     */
    Mono<Void> publish(ApiMeteringEventPayLoad apiMeteringEventPayLoad);
}
