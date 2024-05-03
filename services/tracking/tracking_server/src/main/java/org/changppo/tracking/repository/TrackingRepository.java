package org.changppo.tracking.repository;

import org.changppo.tracking.domain.mongodb.Tracking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackingRepository extends MongoRepository<Tracking, String> {
}
