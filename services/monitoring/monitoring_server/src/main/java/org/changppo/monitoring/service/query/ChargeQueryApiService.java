package org.changppo.monitoring.service.query;

import lombok.RequiredArgsConstructor;
import org.changppo.monioring.domain.api.query.ChargeQueryApi;
import org.changppo.monioring.domain.request.query.ApikeyTotalChargeRequest;
import org.changppo.monioring.domain.request.query.MemberTotalChargeRequest;
import org.changppo.monioring.domain.view.ApiKeyDayChargeView;
import org.changppo.monioring.domain.view.ChargeGraphView;
import org.changppo.monioring.domain.view.DateChargeView;
import org.changppo.monioring.domain.view.TotalChargeView;
import org.changppo.monitoring.util.ValidatedArgs;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@RequiredArgsConstructor
public class ChargeQueryApiService implements ChargeQueryApi {
    private final MongoTemplate mongoTemplate;

    @Override
    @ValidatedArgs
    public TotalChargeView getTotalChargeByApiKeyId(ApikeyTotalChargeRequest request) {
        Instant orAfter = request.timeRange().equalsOrAfter();
        Instant before = request.timeRange().before();
        Long apiKeyId = request.apiKeyId();

        // 전체 금액을 계산하기 위한 aggregation
        Aggregation aggregation = newAggregation(
                match(Criteria.where("api_key_id").is(apiKeyId)
                        .and("timestamp").gte(orAfter).lt(before)),
                group().sum("price").as("totalCharge")
        );

        return mongoTemplate.aggregate(aggregation, "api_record", TotalChargeView.class).getUniqueMappedResult();
    }

    @Override
    public TotalChargeView getTotalCharge(MemberTotalChargeRequest request) {
        Long memberId = request.memberId();
        Instant orAfter = request.timeRange().equalsOrAfter();
        Instant before = request.timeRange().before();

        // 전체 금액을 계산하기 위한 aggregation
        Aggregation aggregation = newAggregation(
                match(Criteria.where("member_id").is(memberId)
                        .and("timestamp").gte(orAfter).lt(before)),
                group().sum("price").as("totalCharge")
        );

        return mongoTemplate.aggregate(aggregation, "api_record", TotalChargeView.class).getUniqueMappedResult();
    }

    @Override
    public ApiKeyDayChargeView getDayChargeByApiKeyId(ApikeyTotalChargeRequest request) {
        // 날짜 별로, API Key 별로 요금을 계산하기 위한 aggregation
        Long apiKeyId = request.apiKeyId();
        Instant orAfter = request.timeRange().equalsOrAfter();
        Instant before = request.timeRange().before();
        // Match stage
        MatchOperation match = Aggregation.match(
                Criteria.where("api_key_id").is(apiKeyId)
                        .and("timestamp").gte(orAfter).lt(before)
        );

        // Project stage
        ProjectionOperation project = Aggregation.project()
                .andInclude("price", "timestamp")
                .andExpression("year(timestamp)").as("year")
                .andExpression("month(timestamp)").as("month")
                .andExpression("dayOfMonth(timestamp)").as("day");

        // Group stage
        GroupOperation group = Aggregation.group(
                Fields.from(Fields.field("year", "$year"),
                        Fields.field("month", "$month"),
                        Fields.field("day", "$day"))
        ).sum("price").as("totalCharge");

        // Project stage for final result
        ProjectionOperation projectFinal = Aggregation.project()
                .andExclude("_id")
                .andExpression("year").as("year")
                .andExpression("month").as("month")
                .andExpression("day").as("day")
                .andExpression("totalCharge").as("totalCharge");

        Aggregation aggregation = Aggregation.newAggregation(match, project, group, projectFinal);

        List<ApiRecordAggregationResult> result = mongoTemplate.aggregate(aggregation, "api_record", ApiRecordAggregationResult.class).getMappedResults();
        List<DateChargeView> dateChargeViews = result.stream()
                .map(r -> {
                    LocalDate date = LocalDate.of(r.year(), r.month(), r.day());
                    BigDecimal totalCharge = BigDecimal.valueOf(r.totalCharge());
                    return new DateChargeView(date, totalCharge);
                })
                .collect(Collectors.toList());
        return new ApiKeyDayChargeView(apiKeyId, dateChargeViews);
    }

    @Override
    public ChargeGraphView getChargeGraphByMemberId(MemberTotalChargeRequest request) {
        return null;
    }


    record ApiRecordAggregationResult(int year, int month, int day, long totalCharge) {
    }
}
