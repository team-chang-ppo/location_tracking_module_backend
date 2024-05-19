package org.changppo.monioring.domain.response;

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
    private static final Response EMPTY_SUCCESS_RESPONSE = new Response(true, "0", null);

        public static Response success() {
            return EMPTY_SUCCESS_RESPONSE;
        }

        public static <T> Response success(T data) {
            return new Response(true, "0", new Success<>(data));
        }

        public static Response failure(String code, String msg) {
        return new Response(false, code, new Failure(msg));
    }
}
