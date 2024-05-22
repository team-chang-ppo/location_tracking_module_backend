package org.changppo.account.service.dto.card;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import org.changppo.account.type.PaymentGatewayType;

import java.time.LocalDateTime;

@Data
public class CardDto {
    private Long id;
    private String type;
    private String issuerCorporation;
    private String bin;
    private PaymentGatewayType paymentGateway;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @QueryProjection
    public CardDto(Long id, String type, String issuerCorporation, String bin, PaymentGatewayType paymentGateway, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.issuerCorporation = issuerCorporation;
        this.bin = bin;
        this.paymentGateway = paymentGateway;
        this.createdAt = createdAt;
    }
}
