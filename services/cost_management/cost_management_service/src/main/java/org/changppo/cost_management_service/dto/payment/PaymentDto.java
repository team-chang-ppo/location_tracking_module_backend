package org.changppo.cost_management_service.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.changppo.cost_management_service.entity.payment.PaymentCardInfo;
import org.changppo.cost_management_service.entity.payment.PaymentStatus;

import java.time.LocalDateTime;
@Getter
@AllArgsConstructor
public class PaymentDto {
    private Long id;
    private Integer amount;
    private PaymentStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime startedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime endedAt;
    private PaymentCardInfo cardInfo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
}
