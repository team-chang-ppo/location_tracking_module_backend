package org.changppo.monioring.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;


@JsonInclude(JsonInclude.Include.NON_EMPTY) // null 또는 empty 값은 출력하지 않는다.
public final class ApiUsageEventPayLoad {

        @JsonProperty(namespace = "event_id", required = true)
        private String eventId;
        @JsonProperty(namespace = "request_time", required = true)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        private Instant requestTime;
        @JsonProperty(namespace = "response_time", required = true)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        private Instant responseTime;
        @JsonProperty(namespace = "request_protocol", required = true)
        private String requestProtocol;
        @JsonProperty(namespace = "request_method", required = true)
        private String requestMethod;
        @JsonProperty(namespace = "request_uri", required = true)
        private String requestUri;
        @JsonProperty(namespace = "response_status")
        private Integer responseStatus;
        @JsonProperty(namespace = "error_code")
        private String errorCode;
        @JsonProperty(namespace = "client_ip")
        private String clientIp;
        @JsonProperty(namespace = "client_agent")
        private String clientAgent;
        @JsonProperty(namespace = "api_key")
        private String apiKey;
        @JsonProperty(namespace = "trace_id")
        private String traceId;

        public ApiUsageEventPayLoad(){
        }

        public ApiUsageEventPayLoad(String eventId, Instant requestTime, Instant responseTime, String requestProtocol, String requestMethod, String requestUri, Integer responseStatus, String errorCode, String clientIp, String clientAgent, String apiKey, String traceId) {
                this.eventId = eventId;
                this.requestTime = requestTime;
                this.responseTime = responseTime;
                this.requestProtocol = requestProtocol;
                this.requestMethod = requestMethod;
                this.requestUri = requestUri;
                this.responseStatus = responseStatus;
                this.errorCode = errorCode;
                this.clientIp = clientIp;
                this.clientAgent = clientAgent;
                this.apiKey = apiKey;
                this.traceId = traceId;
        }

        public String getEventId() {
                return eventId;
        }

        public void setEventId(String eventId) {
                this.eventId = eventId;
        }

        public Instant getRequestTime() {
                return requestTime;
        }

        public void setRequestTime(Instant requestTime) {
                this.requestTime = requestTime;
        }

        public Instant getResponseTime() {
                return responseTime;
        }

        public void setResponseTime(Instant responseTime) {
                this.responseTime = responseTime;
        }

        public String getRequestProtocol() {
                return requestProtocol;
        }

        public void setRequestProtocol(String requestProtocol) {
                this.requestProtocol = requestProtocol;
        }

        public String getRequestMethod() {
                return requestMethod;
        }

        public void setRequestMethod(String requestMethod) {
                this.requestMethod = requestMethod;
        }

        public String getRequestUri() {
                return requestUri;
        }

        public void setRequestUri(String requestUri) {
                this.requestUri = requestUri;
        }

        public Integer getResponseStatus() {
                return responseStatus;
        }

        public void setResponseStatus(Integer responseStatus) {
                this.responseStatus = responseStatus;
        }

        public String getErrorCode() {
                return errorCode;
        }

        public void setErrorCode(String errorCode) {
                this.errorCode = errorCode;
        }

        public String getClientIp() {
                return clientIp;
        }

        public void setClientIp(String clientIp) {
                this.clientIp = clientIp;
        }

        public String getClientAgent() {
                return clientAgent;
        }

        public void setClientAgent(String clientAgent) {
                this.clientAgent = clientAgent;
        }

        public String getApiKey() {
                return apiKey;
        }

        public void setApiKey(String apiKey) {
                this.apiKey = apiKey;
        }

        public String getTraceId() {
                return traceId;
        }

        public void setTraceId(String traceId) {
                this.traceId = traceId;
        }
}
