package org.changppo.monitoring.service;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.monioring.domain.view.MemberChargeGraphView;
import org.changppo.monioring.domain.view.TotalSumView;
import org.changppo.monitoring.dao.HourlyApiUsageCostView;
import org.changppo.monitoring.dao.HourlyApiUsageCostViewRepository;
import org.changppo.monitoring.util.ViewFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CostQueryService {

    private final HourlyApiUsageCostViewRepository costViewRepository;

    public MemberChargeGraphView getChargeGraphView(@NotNull Long memberId, @Nullable Long apiKeyId, @NotNull LocalDate startDate, @NotNull LocalDate endDate) {
        // FETCH
        final List<HourlyApiUsageCostView> fetchedCostViews = getCostViews(memberId, apiKeyId, startDate, endDate);
        if (fetchedCostViews.isEmpty()) {
            log.warn("fetchedCostViews.isEmpty()");
            return MemberChargeGraphView.empty(memberId);
        }

        // CREATE VIEW
        List<MemberChargeGraphView> memberChargeGraphViews = ViewFactory.create(fetchedCostViews);
        int fetchedSize = memberChargeGraphViews.size();
        if (fetchedSize > 1) {
            log.error("memberChargeGraphViews.size() > 1, fetchedSize: {}", fetchedSize);
            throw new IllegalStateException("memberChargeGraphViews.size() > 1");
        }
        return memberChargeGraphViews.get(0);
    }

    public TotalSumView getTotalSum(@NotNull Long memberId, @Nullable Long apiKeyId, @NotNull LocalDate startDate, @NotNull LocalDate endDate) {
        // FETCH
        final List<HourlyApiUsageCostView> fetchedCostViews = getCostViews(memberId, apiKeyId, startDate, endDate);
        if (fetchedCostViews.isEmpty()) {
            log.warn("fetchedCostViews.isEmpty()");
            return new TotalSumView(0L, 0L);
        }

        Long totalCost = fetchedCostViews.stream().map(HourlyApiUsageCostView::getHourlyCost).reduce(0L, Long::sum);
        Long totalCount = fetchedCostViews.stream().map(HourlyApiUsageCostView::getRequestCount).reduce(0L, Long::sum);
        return new TotalSumView(totalCost, totalCount);
    }



    protected List<HourlyApiUsageCostView> getCostViews(@NotNull Long memberId, @Nullable Long apiKeyId, @NotNull LocalDate startDate, @NotNull LocalDate endDate) {
        // FETCH
        if (apiKeyId == null) {
            return costViewRepository.findByMemberIdAndDateBetween(memberId, startDate, endDate);
        } else {
            return costViewRepository.findByMemberIdAndApiKeyIdAndDateBetween(memberId, apiKeyId, startDate, endDate);
        }
    }


}
