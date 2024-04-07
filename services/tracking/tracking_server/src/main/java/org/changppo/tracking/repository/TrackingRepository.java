package org.changppo.tracking.repository;

import org.changppo.tracking.domain.Tracking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TrackingRepository extends MongoRepository<Tracking, String> {
}
