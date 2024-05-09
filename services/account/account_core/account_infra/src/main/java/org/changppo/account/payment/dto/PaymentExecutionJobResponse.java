package org.changppo.account.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentExecutionJobResponse {
    private String key;
    private String CardType;
    private String cardIssuerCorporation;
    private String cardBin;
}
