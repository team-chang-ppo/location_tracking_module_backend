package org.changppo.monioring.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY) // null 또는 empty 값은 출력하지 않는다.
public final class ApiUsageEventPayLoad {

        private String eventId;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        private Instant requestTime;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        private Instant responseTime;
        private String requestProtocol;
        private String requestMethod;
        private String requestUri;
        private Integer responseStatus;
        private String errorCode;
        private String clientIp;
        private String clientAgent;
        private String apiKey;
        private Long apiKeyId;
        private Long memberId;
        private String memberGrade;
        private String traceId;
}
