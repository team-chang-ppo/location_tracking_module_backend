package org.changppo.cost_management_service.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Response {
    private boolean success;
    private String code;
    private Result result;

        public static Response success() {
            return new Response(true, "0",  null);
        }

        public static <T> Response success(T data) {
            return new Response(true, "0", new Success<>(data));
        }

        public static Response failure(String code, String msg) {
        return new Response(false, code, new Failure(msg));
    }
}