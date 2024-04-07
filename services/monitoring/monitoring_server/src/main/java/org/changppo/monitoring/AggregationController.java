package org.changppo.monitoring;

import lombok.RequiredArgsConstructor;
import org.changppo.monitoring.RouteIdCountResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

//TODO : 일단 테스트 용으로 API 하나 뚫음, 추후 API 개발 할 예정
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aggregation/v1")
public class AggregationController {
    private final MongoTemplate mongoTemplate;

    @GetMapping("/ping")
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("pong");
    }


    /*
    var api_key_id = 1;
    var orAfter = ISODate("2024-03-31T00:00:00Z");
    var before = ISODate("2024-04-01T23:59:59Z");

    db.getSiblingDB("monitoring").getCollection("api_record").aggregate([
        {
            $match: {
                "timestamp": {
                    $gte: orAfter, // 시작 timestamp 범위
                    $lt: before
                },
                "api_key_id": api_key_id// 조회하고자 하는 특정 ID
            }
        },
        {
            $group: {
                _id: "route_id", // routeId를 기준으로 그룹화
                count: { $sum: 1 } // 각 routeId 별로 문서 갯수를 세어 count 필드에 저장
            }
        },
        {
            $project :{
                _id: 0,
                route_id: "$_id",
                count: 1
            }
        }
    ])
     */
    @GetMapping("/call_count")
    public ResponseEntity<List<RouteIdCountResult>> getCallCount(@RequestParam("api_key_id") Long apiKeyId,
                                                                 @RequestParam("or_after") Instant orAfter,
                                                                 @RequestParam("before") Instant before){
        MatchOperation match = Aggregation.match(
                Criteria.where("timestamp").gte(orAfter).lt(before).and("api_key_id").is(apiKeyId)
        );
        GroupOperation groupOperation = Aggregation.group("route_id").count().as("count");
        ProjectionOperation projectionOperation = Aggregation.project().andExclude("_id").and("_id").as("routeId").and("count").as("count");
        Aggregation aggregation = Aggregation.newAggregation(match, groupOperation, projectionOperation);
        List<RouteIdCountResult> apiRecord = mongoTemplate.aggregate(aggregation, "api_record", RouteIdCountResult.class)
                .getMappedResults();
        return ResponseEntity.ok(apiRecord);
    }
}
