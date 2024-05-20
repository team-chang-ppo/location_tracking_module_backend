package org.changppo.commons.old;

public class Failure implements Result {
    private String msg;

    public Failure(String msg) {
        this.msg = msg;
    }

    protected Failure() {
    }

    public String getMsg() {
        return msg;
    }
}
