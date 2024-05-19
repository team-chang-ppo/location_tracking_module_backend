package org.changppo.monitoring.controller;

import lombok.RequiredArgsConstructor;
import org.changppo.monioring.domain.view.MemberChargeGraphView;
import org.changppo.monitoring.service.CostQueryService;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/aggregation/v1/")
@RestController
@RequiredArgsConstructor
public class CostQueryController {
    private final CostQueryService costQueryService;

}
