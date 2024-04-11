package org.changppo.tracking.service;

import lombok.RequiredArgsConstructor;
import org.changppo.tracking.domain.mongodb.Tracking;
import org.changppo.tracking.domain.redis.TrackingRedisEntity;
import org.changppo.tracking.exception.TrackingNotFoundException;
import org.changppo.tracking.repository.TrackingRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TrackingCacheService {

    private final TrackingRepository trackingRepository;

    @Cacheable(cacheNames = "tracking", key = "#trackingId")
    public TrackingRedisEntity getTrackingCache(String trackingId) {
        Tracking tracking = trackingRepository.findById(trackingId)
                .orElseThrow(TrackingNotFoundException::new);
        return new TrackingRedisEntity(tracking);
    }

    @CachePut(cacheNames = "tracking", key = "#tracking.id")
    public TrackingRedisEntity updateTrackingCache(Tracking tracking) {
        return new TrackingRedisEntity(tracking);
    }

    @CacheEvict(cacheNames = "tracking", key = "#trackingId")
    public void deleteTrackingCache(String trackingId) {
    }

}
