package org.changppo.monitoring;

import lombok.RequiredArgsConstructor;
import org.changppo.monioring.domain.request.query.ApikeyTotalChargeRequest;
import org.changppo.monioring.domain.view.ApiKeyDayChargeView;
import org.changppo.monioring.domain.view.TotalChargeView;
import org.changppo.monitoring.service.query.ChargeQueryApiService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

//TODO : 일단 테스트 용으로 API 하나 뚫음, 추후 API 개발 할 예정
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aggregation/v1")
public class AggregationController {
    private final MongoTemplate mongoTemplate;
    private final ChargeQueryApiService chargeQueryApiService;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/total_charge") // TODO 권한 체크
    public TotalChargeView getTotalChargeByApiKey(@RequestParam("api_key_id") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Long apiKeyId,
                                          @RequestParam("or_after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant orAfter,
                                          @RequestParam(name = "before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant before) {
        if (before == null) {
            before = Instant.now();
        }
        ApikeyTotalChargeRequest request = new ApikeyTotalChargeRequest(apiKeyId, orAfter, before);
        return chargeQueryApiService.getTotalChargeByApiKeyId(request);
    }

//    @GetMapping("/total_charge") // TODO 권한 체크
//    public TotalChargeView getTotalChargeById(@RequestParam("member_id") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Long memberId,
//                                          @RequestParam("or_after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant orAfter,
//                                          @RequestParam(name = "before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant before) {
//        if (before == null) {
//            before = Instant.now();
//        }
//
//        MemberTotalChargeRequest request = new MemberTotalChargeRequest(memberId, orAfter, before);
//        return chargeQueryApiService.getTotalCharge(request);
//    }

    @GetMapping("/day") // TODO 권한 체크
    public ApiKeyDayChargeView getDayCharge(@RequestParam("api_key_id") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Long apiKeyId,
                                           @RequestParam("or_after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant orAfter,
                                           @RequestParam(name = "before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant before) {
        if (before == null) {
            before = Instant.now();
        }
        ApikeyTotalChargeRequest request = new ApikeyTotalChargeRequest(apiKeyId, orAfter, before);
        return chargeQueryApiService.getDayChargeByApiKeyId(request);
    }
}
