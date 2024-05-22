package org.changppo.commons.old;

public class Success<T> implements Result {
    private T data;

    public Success(T data) {
        this.data = data;
    }

    protected Success() {
    }

    public T getData() {
        return data;
    }
}
