package org.changppo.monitoring.controller;

import lombok.RequiredArgsConstructor;
import org.changppo.monioring.domain.view.MemberChargeGraphView;
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

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/member/{memberId}/charge")
    public MemberChargeGraphView getMemberChargeGraph(@PathVariable Long memberId,
                                                      @RequestParam(required = false) Long apiKeyId,
                                                      @RequestParam LocalDate startDate,
                                                      @RequestParam LocalDate endDate,
                                                      Authentication authentication
                                                      ) {
        Duration duration = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay());
        if (duration.toDays() > 60) {
            throw new IllegalArgumentException("startDate and endDate must be within 60 days");
        }
        return costQueryService.getChargeGraphView(memberId, apiKeyId, startDate, endDate);
    }

}
