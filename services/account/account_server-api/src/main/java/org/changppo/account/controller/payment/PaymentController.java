package org.changppo.account.controller.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.changppo.account.dto.payment.PaymentReadAllRequest;
import org.changppo.account.response.Response;
import org.changppo.account.security.PrincipalHandler;
import org.changppo.account.service.dto.payment.PaymentDto;
import org.changppo.account.service.payment.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments/v1")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/repayment/{id}")
    public ResponseEntity<Response> repayment(@PathVariable(name = "id") Long id) {
        PaymentDto paymentDto = paymentService.repayment(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success(paymentDto));
    }

    @GetMapping("/member/me")
    public ResponseEntity<Response> readAll(@Valid @ModelAttribute PaymentReadAllRequest req) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success(paymentService.readAll(PrincipalHandler.extractId(), req)));
    }

    @GetMapping("/member/{id}")
    public ResponseEntity<Response> readAll(@PathVariable(name = "id") Long id, @Valid @ModelAttribute PaymentReadAllRequest req) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success(paymentService.readAll(id, req)));
    }
}
