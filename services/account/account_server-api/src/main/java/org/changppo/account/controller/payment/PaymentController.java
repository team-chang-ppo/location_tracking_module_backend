package org.changppo.account.controller.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.changppo.account.dto.payment.PaymentListDto;
import org.changppo.account.dto.payment.PaymentReadAllRequest;
import org.changppo.account.service.dto.apikey.ApiKeyDto;
import org.changppo.utils.response.body.Response;
import org.changppo.account.security.PrincipalHandler;
import org.changppo.account.service.dto.payment.PaymentDto;
import org.changppo.account.service.payment.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        PaymentListDto paymentListDto = paymentService.readAll(PrincipalHandler.extractId(), req);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success(paymentListDto));
    }

    @GetMapping("/member/{id}")
    public ResponseEntity<Response> readAll(@PathVariable(name = "id") Long id, @Valid @ModelAttribute PaymentReadAllRequest req) {
        PaymentListDto paymentListDto = paymentService.readAll(id, req);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success(paymentListDto));
    }

    @GetMapping("/list")  // 사용자에게 제공 X
    public ResponseEntity<Response> readList(Pageable pageable) {
        Page<PaymentDto> paymentDtos = paymentService.readList(pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success(paymentDtos));
    }
}
