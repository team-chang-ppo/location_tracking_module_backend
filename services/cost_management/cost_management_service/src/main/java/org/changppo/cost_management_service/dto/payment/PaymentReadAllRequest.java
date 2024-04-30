package org.changppo.cost_management_service.dto.payment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentReadAllRequest {
    @Positive(message = "{paymentReadAllRequest.lastPaymentId.positive}")
    @Max(value = Long.MAX_VALUE - 1, message = "{paymentReadAllRequest.lastPaymentId.maxValue}")
    private Long lastPaymentId = Long.MAX_VALUE - 1;

    @NotNull(message = "{paymentReadAllRequest.size.notNull}")
    @Positive(message = "{paymentReadAllRequest.size.positive}")
    @Max(value = Integer.MAX_VALUE - 1, message = "{paymentReadAllRequest.size.maxValue}")
    private Integer size;
}
