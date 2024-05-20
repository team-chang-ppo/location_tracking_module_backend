package org.changppo.commons.old;


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

    public Response(boolean success, String code, Result result) {
        this.success = success;
        this.code = code;
        this.result = result;
    }

    protected Response() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
