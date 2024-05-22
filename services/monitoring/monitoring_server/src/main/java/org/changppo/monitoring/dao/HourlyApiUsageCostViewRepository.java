package org.changppo.monitoring.dao;

import org.springframework.data.repository.Repository;

import java.time.LocalDate;
import java.util.List;

@org.springframework.stereotype.Repository
public interface HourlyApiUsageCostViewRepository extends Repository<HourlyApiUsageCostView, CostViewId> {
    List<HourlyApiUsageCostView> findByMemberIdAndDateBetween (Long memberId, LocalDate startDate, LocalDate endDate);

    List<HourlyApiUsageCostView> findByMemberIdAndApiKeyIdAndDateBetween(Long memberId, Long apiKeyId, LocalDate startDate, LocalDate endDate);
}
