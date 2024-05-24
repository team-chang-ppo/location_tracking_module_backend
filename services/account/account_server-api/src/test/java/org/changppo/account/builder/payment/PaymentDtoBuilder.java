package org.changppo.account.builder.payment;

import org.changppo.account.entity.payment.PaymentCardInfo;
import org.changppo.account.service.dto.payment.PaymentDto;
import org.changppo.account.type.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDtoBuilder {

    public static PaymentDto buildPaymentDto() {
        return new PaymentDto(1L, new BigDecimal("100.00"), PaymentStatus.COMPLETED_PAID, LocalDateTime.now(), LocalDateTime.now().plusHours(1) ,
                new PaymentCardInfo("type", "issuerCorporation", "bin"), LocalDateTime.now());

    }
}
