package org.changppo.account.controller.card.paymentgateway.kakaopay;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.changppo.account.aop.AssignMemberId;
import org.changppo.account.service.dto.card.CardDto;
import org.changppo.account.dto.card.kakaopay.*;
import org.changppo.account.response.Response;
import org.changppo.account.service.card.paymentgateway.kakaopay.KakaopayCardService;
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
    public ResponseEntity<Response> registerReady(@Valid @RequestBody KakaopayCardRegisterReadyRequest req) {
        KakaopayCardRegisterRedirectResponse kakaopayCardRegisterRedirectResponse = kakaopayCardService.registerReady(req);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success(kakaopayCardRegisterRedirectResponse));
    }

    @AssignMemberId
    @GetMapping("/register/approve")
    public ResponseEntity<Response> registerApprove(@Valid @ModelAttribute KakaopayCardRegisterApproveRequest req) {
        CardDto cardDto = kakaopayCardService.registerApprove(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.success(cardDto));
    }

    @AssignMemberId
    @GetMapping("/register/cancel")
    public ResponseEntity<Response> registerCancel(@Valid @ModelAttribute KakaopayCardRegisterCancelRequest req) {
        kakaopayCardService.registerCancel(req);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success());
    }

    @AssignMemberId
    @GetMapping("/register/fail")
    public void registerFail(@Valid @ModelAttribute KakaopayCardRegisterFailRequest req) {
        kakaopayCardService.registerFail(req);
    }
}
