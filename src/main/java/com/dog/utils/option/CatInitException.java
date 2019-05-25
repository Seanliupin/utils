package com.dog.utils.option;

public class CatInitException extends OptionException {

    public CatInitException(String message) {
        super(message, 300);
    }

    public CatInitException(String msg, int code) {
        super(msg, code);
    }
}
