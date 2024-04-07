package org.changppo.monitoring;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiRecordRepository extends MongoRepository<ApiRecordDocument, String> {

}
