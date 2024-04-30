package org.changppo.monioring.domain.view;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * @param currency 3자리 화폐 코드 (ISO 4217)
 * @see <a href="https://en.wikipedia.org/wiki/ISO_4217">ISO 4217</a>
 */
public record TotalChargeView(
        Currency currency,
        BigDecimal totalCharge
) {
}
