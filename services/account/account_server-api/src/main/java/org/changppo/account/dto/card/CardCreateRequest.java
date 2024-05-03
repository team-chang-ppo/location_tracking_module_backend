package org.changppo.account.dto.card;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.changppo.account.type.PaymentGatewayType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardCreateRequest {
    @NotNull(message = "{cardCreateRequest.key.notNull}")
    private String key;
    @NotNull(message = "{cardCreateRequest.type.notNull}")
    private String type;
    @NotNull(message = "{cardCreateRequest.acquirerCorporation.notNull}")
    private String acquirerCorporation;
    @NotNull(message = "{cardCreateRequest.issuerCorporation.notNull}")
    private String issuerCorporation;
    @NotNull(message = "{cardCreateRequest.bin.notNull}")
    private String bin;
    @NotNull(message = "{cardCreateRequest.PaymentGatewayType.notNull}")
    private PaymentGatewayType PaymentGatewayType;
    @NotNull(message = "{cardCreateRequest.memberId.notNull}")
    private Long memberId;
}
