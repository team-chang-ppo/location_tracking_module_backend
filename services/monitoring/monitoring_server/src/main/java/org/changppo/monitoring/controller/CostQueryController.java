package org.changppo.monitoring.controller;

import lombok.RequiredArgsConstructor;
import org.changppo.monioring.domain.view.MemberChargeGraphView;
import org.changppo.monitoring.config.CostQueryProperties;
import org.changppo.monitoring.service.CostQueryService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;

@RequestMapping("/api/aggregation/v1")
@RestController
@RequiredArgsConstructor
public class CostQueryController {
    private final CostQueryService costQueryService;
    private final CostQueryProperties costQueryProperties;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/member/{memberId}/charge")
    public MemberChargeGraphView getMemberChargeGraph(@PathVariable Long memberId,
                                                      @RequestParam(required = false) Long apiKeyId,
                                                      @RequestParam LocalDate startDate,
                                                      @RequestParam LocalDate endDate
                                                      ) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before endDate");
        }
        long days = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays();
        if (days > costQueryProperties.getMaxDuration().toDays()) {
            throw new IllegalArgumentException("startDate and endDate must be within " + costQueryProperties.getMaxDuration().toDays() + " days");
        }

        return costQueryService.getChargeGraphView(memberId, apiKeyId, startDate, endDate);
    }

}
