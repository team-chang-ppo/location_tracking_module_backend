package org.changppo.monioring.domain;

public record ApiMeteringEventPayLoad(
        String eventId,
        Long memberId,
        Long apiKeyId,
        String routeId
) {
}
