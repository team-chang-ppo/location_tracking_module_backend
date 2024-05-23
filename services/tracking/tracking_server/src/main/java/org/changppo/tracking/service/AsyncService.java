package org.changppo.tracking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.tracking.domain.mongodb.Tracking;
import org.changppo.tracking.domain.redis.CoordinateRedisEntity;
import org.changppo.tracking.exception.TrackingNotFoundException;
import org.changppo.tracking.repository.CoordinatesRepository;
import org.changppo.tracking.repository.RedisRepository;
import org.changppo.tracking.repository.TrackingRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class AsyncService {

    private final RedisRepository redisRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final TrackingRepository trackingRepository;
    private final TrackingCacheService trackingCacheService;

    @Async
    public CompletableFuture<String> terminateTrackingAsync(String trackingId) {
        return CompletableFuture.supplyAsync(() -> {
            Tracking tracking = trackingRepository.findById(trackingId).orElseThrow(TrackingNotFoundException::new);
            tracking.updateEndedAt();

            trackingCacheService.updateTrackingCache(tracking);
            Tracking savedTracking = trackingRepository.save(tracking);
            return savedTracking.getId();
        });
    }

    @Async
    public CompletableFuture<Void> processCoordinatesAsync(String trackingId) {
        return terminateTrackingAsync(trackingId)
                .thenCompose(savedTrackingId ->
                        CompletableFuture.supplyAsync(() -> redisRepository.findAll(savedTrackingId))
                                .thenApplyAsync(objectList ->
                                        objectList.stream()
                                                .filter(obj -> obj instanceof CoordinateRedisEntity)
                                                .map(obj -> (CoordinateRedisEntity) obj)
                                                .map(CoordinateRedisEntity::toCoordinates)
                                                .toList()
                                )
                                .thenAcceptAsync(coordinatesRepository::saveAll)
                );
    }
}
