package org.changppo.monioring.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;


@Getter @Setter
@NoArgsConstructor
public class HourlyApiUsageEvent {
    private Long apiEndpointId;
    @NotNull
    @Positive
    private Long memberId;
    @NotNull
    @Positive
    private Long apiKeyId;
    @NotNull
    private LocalDate date;
    @NotNull
    @Valid
    private Window window;
    @NotNull
    @Positive
    private Long count;

    @Getter @Setter
    @NoArgsConstructor
    public static class Window {
        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        private LocalDateTime start;
        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        private LocalDateTime end;
    }


}
