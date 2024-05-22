package org.changppo.account.controller.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.changppo.account.dto.payment.PaymentListDto;
import org.changppo.account.dto.payment.PaymentReadAllRequest;
import org.changppo.account.security.PrincipalHandler;
import org.changppo.account.service.dto.payment.PaymentDto;
import org.changppo.account.service.payment.PaymentService;
import org.changppo.commons.ResponseBody;
import org.changppo.commons.SuccessResponseBody;
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
    public ResponseEntity<ResponseBody<PaymentDto>> repayment(@PathVariable(name = "id") Long id) {
        PaymentDto paymentDto = paymentService.repayment(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>(paymentDto));
    }

    @GetMapping("/member/me")
    public ResponseEntity<ResponseBody<PaymentListDto>> readAll(@Valid @ModelAttribute PaymentReadAllRequest req) {
        PaymentListDto paymentListDto = paymentService.readAll(PrincipalHandler.extractId(), req);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>(paymentListDto));
    }

    @GetMapping("/member/{id}")
    public ResponseEntity<ResponseBody<PaymentListDto>> readAll(@PathVariable(name = "id") Long id, @Valid @ModelAttribute PaymentReadAllRequest req) {
        PaymentListDto paymentListDto = paymentService.readAll(id, req);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>(paymentListDto));
    }

    @GetMapping("/list")  // 사용자에게 제공 X
    public ResponseEntity<ResponseBody<Page<PaymentDto>>> readList(Pageable pageable) {
        Page<PaymentDto> paymentDtos = paymentService.readList(pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SuccessResponseBody<>(paymentDtos));
    }
}
