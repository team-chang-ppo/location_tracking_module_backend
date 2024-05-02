package org.changppo.account.service.dto.card;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.changppo.account.type.PaymentGatewayType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CardDto {
    private Long id;
    private String type;
    private String issuerCorporation;
    private String bin;
    private PaymentGatewayType paymentGateway;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
}
