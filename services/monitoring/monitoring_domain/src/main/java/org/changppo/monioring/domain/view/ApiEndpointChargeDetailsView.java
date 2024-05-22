package org.changppo.monioring.domain.view;

import java.util.List;

public record ApiEndpointChargeDetailsView(
        Long apiEndpointId,
        Long count,
        Long costPerCount,
        Long cost
) {
}
