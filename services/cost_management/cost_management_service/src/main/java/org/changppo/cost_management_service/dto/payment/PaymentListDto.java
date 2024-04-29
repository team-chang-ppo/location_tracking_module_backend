package org.changppo.cost_management_service.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PaymentListDto {
    private int numberOfElements;

    private boolean hasNext;

    private List<PaymentDto> apiKeyList;
}