package org.changppo.cost_management_service.controller.payment;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.dto.payment.PaymentDto;
import org.changppo.cost_management_service.response.Response;
import org.changppo.cost_management_service.service.payment.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments/v1")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{id}")
    public ResponseEntity<Response> repayment(@PathVariable(name = "id") Long id) {
        PaymentDto paymentDto = paymentService.repayment(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.success(paymentDto));
    }
}
