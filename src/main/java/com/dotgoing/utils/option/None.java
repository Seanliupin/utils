package com.dotgoing.utils.option;

import java.util.function.Function;

public final class None<T> extends Option<T> {

    private final Exception defaultError = new Exception("none default error");
    private final Exception err;

    public None() {
        err = defaultError;
    }

    public None(Exception err) {
        this.err = err;
    }

    public Exception getErr() {
        return err;
    }

    @Override
    public <R> Option<R> map(Function<? super T, ? extends R> transformer) {
        return new None<>();
    }

    @Override
    public <R> Option<R> flatMap(Function<? super T, ? extends Option<? extends R>> transformer) {
        return new None<>();
    }

    @Override
    public T value() {
        throw new RuntimeException("none has no value");
    }

    @Override
    public boolean hasValue() {
        return false;
    }

    @Override
    public String toString() {
        return "None()";
    }
}

