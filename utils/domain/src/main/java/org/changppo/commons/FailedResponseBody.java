package org.changppo.commons;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("false")
public final class FailedResponseBody<T> extends ResponseBody<T> {
    private ErrorPayload result;

    protected FailedResponseBody() {
    }

    public FailedResponseBody(String code, String msg) {
        super.setCode(code);
        this.result = new ErrorPayload(msg);
    }

    public ErrorPayload getResult() {
        return result;
    }


    public static class ErrorPayload {
        private String msg;

        public ErrorPayload(String msg) {
            this.msg = msg;
        }

        protected ErrorPayload() {
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
