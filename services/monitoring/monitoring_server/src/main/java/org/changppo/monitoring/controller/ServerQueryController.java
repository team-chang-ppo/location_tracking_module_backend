package org.changppo.monitoring.controller;

import lombok.RequiredArgsConstructor;
import org.changppo.monioring.domain.error.InvalidInputValueException;
import org.changppo.monioring.domain.error.QueryDurationExceededException;
import org.changppo.monioring.domain.view.MemberChargeGraphView;
import org.changppo.monitoring.config.CostQueryProperties;
import org.changppo.monitoring.service.CostQueryService;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;

@RequestMapping("/private/aggregation/v1")
@RestController
@RequiredArgsConstructor
public class ServerQueryController {
    private final CostQueryService costQueryService;
    private final CostQueryProperties costQueryProperties;

    @GetMapping("/member/{memberId}/charge")
    public MemberChargeGraphView getMemberChargeGraph(@PathVariable Long memberId,
                                                      @RequestParam(required = false) Long apiKeyId,
                                                      @RequestParam LocalDate startDate,
                                                      @RequestParam LocalDate endDate
    ) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidInputValueException();
        }
        long days = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays();
        if (days > costQueryProperties.getMaxDuration().toDays()) {
            throw new QueryDurationExceededException();
        }

        return costQueryService.getChargeGraphView(memberId, apiKeyId, startDate, endDate);
    }

}
