package org.changppo.monitoring.controller;

import lombok.RequiredArgsConstructor;
import org.changppo.commons.SuccessResponseBody;
import org.changppo.monioring.domain.error.InvalidInputValueException;
import org.changppo.monioring.domain.error.QueryDurationExceededException;
import org.changppo.monioring.domain.view.MemberChargeGraphView;
import org.changppo.monioring.domain.view.TotalSumView;
import org.changppo.monitoring.config.CostQueryProperties;
import org.changppo.monitoring.service.CostQueryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

@RequestMapping("/private/aggregation/v1")
@RestController
@RequiredArgsConstructor
public class ServerQueryController {
    private final CostQueryService costQueryService;
    private final CostQueryProperties costQueryProperties;

    @GetMapping("/member/{memberId}/charge")
    public SuccessResponseBody<MemberChargeGraphView> getMemberChargeGraph(@PathVariable Long memberId,
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

        return new SuccessResponseBody<>(costQueryService.getChargeGraphView(memberId, apiKeyId, startDate, endDate));
    }

    @GetMapping("/member/{memberId}/charge/total")
    public SuccessResponseBody<TotalSumView> getMemberTotalCost(@PathVariable Long memberId,
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

        return new SuccessResponseBody<>(costQueryService.getTotalSum(memberId, apiKeyId, startDate, endDate));
    }
}
