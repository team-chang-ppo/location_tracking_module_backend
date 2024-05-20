package org.changppo.commons;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("true")
public final class SuccessResponseBody<T> extends ResponseBody<T>{
    private T result;

    protected SuccessResponseBody() {
        result = null;
    }

    public SuccessResponseBody(T result) {
        this.result = result;
    }

    public T getResult() {
        return result;
    }
}
