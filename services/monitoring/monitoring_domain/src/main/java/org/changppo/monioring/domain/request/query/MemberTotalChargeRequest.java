package org.changppo.monioring.domain.request.query;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.changppo.monioring.domain.TimeRange;

import java.time.Instant;

/**
 * @param memberId 회원 ID , never null
 * @param timeRange 조회할 시간 구간 , never null
 */
public record MemberTotalChargeRequest(
        @NotNull
        Long memberId,
        @NotNull @Valid
        TimeRange timeRange
) {

        public MemberTotalChargeRequest(@NotNull
                                        Long memberId, @NotNull @Valid
                                        TimeRange timeRange) {
                this.memberId = memberId;
                this.timeRange = timeRange;
        }

        public MemberTotalChargeRequest(
                @NotNull Long memberId,
                @NotNull Instant orAfter,
                @NotNull Instant before) {
                this(memberId, new TimeRange(orAfter, before));
        }
}
