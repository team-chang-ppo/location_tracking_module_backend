package org.changppo.tracking.repository;


import org.changppo.tracking.domain.mongodb.Coordinates;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CoordinatesRepository extends MongoRepository<Coordinates, String> {

    @Aggregation(pipeline = {
            "{ $match: { 'trackingId': ?0 }}",
            "{ $sort: { 'createdAt': -1 }}",
            "{ $limit: 1 }"
    })
    Optional<Coordinates> findTopByTrackingIdOrderByCreatedAtDesc(String trackingId);
}
