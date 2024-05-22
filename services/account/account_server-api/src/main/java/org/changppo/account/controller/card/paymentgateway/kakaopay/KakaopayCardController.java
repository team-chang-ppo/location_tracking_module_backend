package org.changppo.account.controller.card.paymentgateway.kakaopay;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.changppo.account.aop.AssignMemberId;
import org.changppo.account.dto.card.kakaopay.*;
import org.changppo.account.service.card.paymentgateway.kakaopay.KakaopayCardService;
import org.changppo.account.service.dto.card.CardDto;
import org.changppo.commons.ResponseBody;
import org.changppo.commons.SuccessResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards/v1/kakaopay")
public class KakaopayCardController {

    private final KakaopayCardService kakaopayCardService;

    @AssignMemberId
    @PostMapping("/register/ready")
    public ResponseEntity<ResponseBody<KakaopayCardRegisterRedirectResponse>> registerReady(@Valid @RequestBody KakaopayCardRegisterReadyRequest req) {
        KakaopayCardRegisterRedirectResponse kakaopayCardRegisterRedirectResponse = kakaopayCardService.registerReady(req);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>(kakaopayCardRegisterRedirectResponse));
    }

    @AssignMemberId
    @GetMapping("/register/approve")
    public ResponseEntity<ResponseBody<CardDto>> registerApprove(@Valid @ModelAttribute KakaopayCardRegisterApproveRequest req) {
        CardDto cardDto = kakaopayCardService.registerApprove(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SuccessResponseBody<>(cardDto));
    }

    @AssignMemberId
    @GetMapping("/register/cancel")
    public ResponseEntity<ResponseBody<Void>> registerCancel(@Valid @ModelAttribute KakaopayCardRegisterCancelRequest req) {
        kakaopayCardService.registerCancel(req);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>());
    }

    @AssignMemberId
    @GetMapping("/register/fail")
    public void registerFail(@Valid @ModelAttribute KakaopayCardRegisterFailRequest req) {
        kakaopayCardService.registerFail(req);
    }
}
