package org.changppo.monitoring.controller;

import lombok.RequiredArgsConstructor;
import org.changppo.monitoring.service.CostQueryService;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/aggregation/v1")
@RestController
@RequiredArgsConstructor
public class CostQueryController {
    private final CostQueryService costQueryService;

//    @GetMapping("/member/{memberId}/charge")
//    public MemberChargeGraphView getMemberChargeGraph(@PathVariable Long memberId,
//                                                      @RequestParam(required = false) Long apiKeyId,
//                                                      @RequestParam LocalDate startDate,
//                                                      @RequestParam LocalDate endDate,
//                                                      HttpSession session
//                                                      ) {
//        // 세션 정보가 없는지
//        Object memberIdAttr = session.getAttribute(RemoteSession.MEMBER_ID_FIELD);
//        Object rolesAttr = session.getAttribute(RemoteSession.ROLES_FIELD);
//
//        costQueryService.getChargeGraphView(memberId, apiKeyId, startDate, endDate);
//
//    }

}
