package org.changppo.tracking.service;

import lombok.RequiredArgsConstructor;
import org.changppo.tracking.domain.redis.CoordinateRedisEntity;
import org.changppo.tracking.repository.CoordinatesRepository;
import org.changppo.tracking.repository.RedisRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class AsyncService {

    private final RedisRepository redisRepository;
    private final CoordinatesRepository coordinatesRepository;

    @Async
    public CompletableFuture<Void> processCoordinatesAsync(String trackingId) {
        return CompletableFuture.supplyAsync(() -> redisRepository.findAll(trackingId))
                .thenApplyAsync(objectList ->
                        objectList.stream()
                                .filter(obj -> obj instanceof CoordinateRedisEntity)
                                .map(obj -> (CoordinateRedisEntity) obj)
                                .map(CoordinateRedisEntity::toCoordinates)
                                .toList()
                )
                .thenAcceptAsync(coordinatesRepository::saveAll);
    }
}
