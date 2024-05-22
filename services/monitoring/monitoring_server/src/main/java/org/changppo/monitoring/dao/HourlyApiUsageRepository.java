package org.changppo.monitoring.dao;

import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.List;

@org.springframework.stereotype.Repository
public interface HourlyApiUsageRepository extends Repository<HourlyApiUsageEntity, Long>{

    // bulk insert
    void saveAll(Iterable<HourlyApiUsageEntity> hourlyApiUsageEntities);

}
