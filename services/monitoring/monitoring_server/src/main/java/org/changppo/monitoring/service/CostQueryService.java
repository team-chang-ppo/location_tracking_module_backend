package org.changppo.monitoring.service;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.monioring.domain.view.MemberChargeGraphView;
import org.changppo.monitoring.dao.HourlyApiUsageCostView;
import org.changppo.monitoring.dao.HourlyApiUsageCostViewRepository;
import org.changppo.monitoring.util.ViewFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CostQueryService {

    private final HourlyApiUsageCostViewRepository costViewRepository;

    public MemberChargeGraphView getChargeGraphView(@NotNull Long memberId, @Nullable Long apiKeyId, @NotNull LocalDate startDate, @NotNull LocalDate endDate) {
        Assert.notNull(memberId, "memberId must not be null");
        Assert.notNull(startDate, "startDate must not be null");
        Assert.notNull(endDate, "endDate must not be null");

        // FETCH
        final List<HourlyApiUsageCostView> fetchedCostViews;
        if (apiKeyId == null) {
            fetchedCostViews = costViewRepository.findByMemberIdAndDateBetween(memberId, startDate, endDate);
        } else {
            fetchedCostViews = costViewRepository.findByMemberIdAndApiKeyIdAndDateBetween(memberId, apiKeyId, startDate, endDate);
        }

        // CREATE VIEW
        List<MemberChargeGraphView> memberChargeGraphViews = ViewFactory.create(fetchedCostViews);
        if (memberChargeGraphViews.size() != 1) {
            log.error("memberChargeGraphViews.size() != 1");
            throw new IllegalStateException("memberChargeGraphViews.size() != 1");
        }
        return memberChargeGraphViews.get(0);
    }
}
