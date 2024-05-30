package org.changppo.monitoring.util;

import jakarta.validation.constraints.NotNull;
import org.changppo.monioring.domain.view.*;
import org.changppo.monitoring.dao.HourlyApiUsageCostView;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 굉장히 복잡한 View 생성 로직을 담당하는 Factory 클래스, 각 메서드를 recursive하게 호출하여 View를 생성한다.
 */
public class ViewFactory {

    public static List<MemberChargeGraphView> create(@NotNull List<HourlyApiUsageCostView> hourlyApiUsageCostViews) {
        Assert.notNull(hourlyApiUsageCostViews, "hourlyApiUsageCostViews must not be null");
        return hourlyApiUsageCostViews.stream()
                .collect(Collectors.groupingBy(HourlyApiUsageCostView::getMemberId))
                .entrySet().stream()
                .map(entry -> {
                    Long memberId = entry.getKey();
                    List<HourlyApiUsageCostView> memberHourlyApiUsageCostViews = entry.getValue();
                    return createMemberChargeGraphView(memberId, memberHourlyApiUsageCostViews);
                })
                .collect(Collectors.toList());
    }

    protected static MemberChargeGraphView createMemberChargeGraphView(Long memberId, List<HourlyApiUsageCostView> hourlyApiUsageCostViews) {
        List<ApiKeyChargeView> apiKeyChargeViews = hourlyApiUsageCostViews.stream()
                .collect(Collectors.groupingBy(HourlyApiUsageCostView::getApiKeyId))
                .entrySet().stream()
                .map(entry -> {
                    Long apiKeyId = entry.getKey();
                    List<HourlyApiUsageCostView> apiKeyHourlyApiUsageCostViews = entry.getValue();
                    return createApiKeyChargeView(apiKeyId, apiKeyHourlyApiUsageCostViews);
                })
                .collect(Collectors.toList());
        return new MemberChargeGraphView(memberId,apiKeyChargeViews);
    }

    protected static ApiKeyChargeView createApiKeyChargeView(Long apiKeyId, List<HourlyApiUsageCostView> hourlyApiUsageCostViews) {
        List<DayChargeView> dayChargeViews = hourlyApiUsageCostViews.stream()
                .collect(Collectors.groupingBy(HourlyApiUsageCostView::getDate))
                .entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<HourlyApiUsageCostView> dailyHourlyApiUsageCostViews = entry.getValue();
                    return createDayChargeView(date, dailyHourlyApiUsageCostViews);
                })
                .collect(Collectors.toList());
        return new ApiKeyChargeView(apiKeyId, dayChargeViews);
    }

    protected static DayChargeView createDayChargeView(LocalDate date, List<HourlyApiUsageCostView> hourlyApiUsageCostViews) {
        List<HourChargeView> hourChargeViews = hourlyApiUsageCostViews.stream()
                .collect(Collectors.groupingBy(HourlyApiUsageCostView::getHour))
                .entrySet().stream()
                .map(entry -> {
                    Integer hour = entry.getKey();
                    List<HourlyApiUsageCostView> apiUsageCostViews = entry.getValue();
                    return createHourChargeView(hour, apiUsageCostViews);
                })
                .collect(Collectors.toList());
        return new DayChargeView(date, hourChargeViews);
    }

    protected static HourChargeView createHourChargeView(Integer hour, List<HourlyApiUsageCostView> hourlyApiUsageCostViews) {
        Long cost = hourlyApiUsageCostViews.stream().map(HourlyApiUsageCostView::getHourlyCost).reduce(0L, Long::sum);
        Long count = hourlyApiUsageCostViews.stream().map(HourlyApiUsageCostView::getRequestCount).reduce(0L, Long::sum);
        return new HourChargeView(hour, cost, count);
    }
}
