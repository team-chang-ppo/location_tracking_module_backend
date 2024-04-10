package org.changppo.tracking.exception.common;

import lombok.*;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private final String timestamp = LocalDateTime.now().toString();
    private String code;
    private int status;
    private String message;

    @Builder
    public ErrorResponse(String code, int status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public static ErrorResponse of(@NonNull ErrorCode errorCode, @Nullable String message) {
        if(message == null)
            message = errorCode.getMessage();

        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .status(errorCode.getStatus())
                .message(message)
                .build();
    }
}
