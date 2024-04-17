package org.changppo.monioring.domain.request.query;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.changppo.monioring.domain.TimeRange;
import org.changppo.monioring.domain.validation.Iso4217CurrencyCode;

/**
 * @param apiKeyId API Key ID , never null
 * @param timeRange 조회할 시간 구간 , never null
 * @param currency 화폐 단위 (ISO 4217) , nullable
 */
public record ApikeyTotalChargeRequest(
        @NotNull
        Long apiKeyId,
        @NotNull @Valid
        TimeRange timeRange,
        @Iso4217CurrencyCode
        String currency
) {
}
