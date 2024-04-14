package org.changppo.monioring.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;

import java.time.Instant;

public record TimeRange(
        @NotNull @Past
        Instant equalsOrAfter, // 이상
        @NotNull @PastOrPresent
        Instant before // 미만
) {
    public boolean contains(Instant instant) {
        return !instant.isBefore(equalsOrAfter) && instant.isBefore(before);
    }

    public TimeRange{
        if(equalsOrAfter.isAfter(before)){
            throw new IllegalArgumentException("equalsOrAfter must be before before");
        }
    }

}
