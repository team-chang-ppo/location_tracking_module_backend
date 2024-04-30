package org.changppo.cost_management_service.entity.payment;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCardInfo { // 카드 정보는 최소한의 정보만 저장
    private String type;
    private String issuerCorporation;
    private String bin;
}