package org.changppo.monioring.domain;

public interface GatewayConstant {
    String API_KEY_HEADER = "X-API-KEY";

    String TRACE_ID_HEADER = "X-TRACE-ID";

    /*
    의존성
    - 다른 서비스
    - kafka-connector
     */
    String API_METERING_TOPIC = "api-usage-trace";
}
