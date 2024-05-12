package org.changppo.monioring.domain.request.query;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.changppo.monioring.domain.TimeRange;

import java.time.Instant;

/**
 * @param apiKeyId API Key ID , never null
 * @param timeRange 조회할 시간 구간 , never null
 */
public record ApikeyTotalChargeRequest(
        @NotNull
        Long apiKeyId,
        @NotNull @Valid
        TimeRange timeRange
) {

        public ApikeyTotalChargeRequest(@NotNull
                                        Long apiKeyId, @NotNull @Valid
                                        TimeRange timeRange) {
                this.apiKeyId = apiKeyId;
                this.timeRange = timeRange;
        }

        public ApikeyTotalChargeRequest(
                @NotNull Long apiKeyId,
                @NotNull Instant orAfter,
                @NotNull Instant before) {
                this(apiKeyId, new TimeRange(orAfter, before));
        }
}
