package com.dog.utils.option;

import java.util.function.Function;

public final class None<T> extends Option<T> {

    private final Exception defaultError = new OptionException("none default error");
    private final Exception err;

    None() {
        err = defaultError;
    }

    None(Exception err) {
        this.err = err;
    }

    None(String err) {
        this.err = new OptionException(err);
    }

    @Override
    public Exception error() {
        return err;
    }

    @Override
    public <R> Option<R> map(Function<? super T, ? extends R> transformer) {
        return Option.empty();
    }

    @Override
    public <R> Option<R> flatMap(Function<? super T, ? extends Option<? extends R>> transformer) {
        return Option.empty();
    }

    @Override
    public T get() {
        throw new RuntimeException("none has no value");
    }

    @Override
    public boolean hasValue() {
        return false;
    }

    @Override
    public String toString() {
        return "None(" + err.toString() + ")";
    }
}

