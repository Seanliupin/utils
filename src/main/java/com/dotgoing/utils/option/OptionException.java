package com.dotgoing.utils.option;

public class OptionException extends RuntimeException {
    private int code = 0;

    public OptionException(String msg) {
        this(msg, 0);
    }

    public OptionException(String msg, int code) {
        super(msg);
        this.code = code;
    }

    public OptionException(Exception e) {
        this(e.getMessage(), 004);
    }

    public OptionException() {
        super();
    }

    public int getCode() {
        return code;
    }
}
