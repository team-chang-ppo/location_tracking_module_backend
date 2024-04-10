package org.changppo.tracking.repository;


import org.changppo.tracking.domain.mongodb.Coordinates;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CoordinatesRepository extends MongoRepository<Coordinates, String> {

    // TODO : limit 설정이 없어보여서 우선은 배열로 받아옴. 추후 발견하면 수정
    @Query(value = "{ 'trackingId' : ?0}", sort = "{ 'createdAt' : -1 }")
    Set<Coordinates> findByTrackingIdOrderByCreatedAtDesc(String trackingId);
}
