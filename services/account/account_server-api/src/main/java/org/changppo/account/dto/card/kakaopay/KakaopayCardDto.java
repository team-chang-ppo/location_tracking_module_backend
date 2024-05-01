package org.changppo.account.dto.card.kakaopay;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KakaopayCardDto {
    private String type;
    private String acquirerCorporation;
    private String issuerCorporation;
    private String bin;
}
