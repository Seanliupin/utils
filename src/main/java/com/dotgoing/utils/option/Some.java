package com.dotgoing.utils.option;

import java.util.function.Function;

public final class Some<T> extends Option<T> {
    private final T t;

    public Some(T t) {
        this.t = t;
    }

    @Override
    public <R> Option<R> map(Function<? super T, ? extends R> transformer) {
        return new Some<>(transformer.apply(value()));
    }

    @Override
    public <R> Option<R> flatMap(Function<? super T, ? extends Option<? extends R>> transformer) {
        return (Option<R>) transformer.apply(value());
    }

    @Override
    public T value() {
        return t;
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public String toString() {
        return "Some(" + t + ")";
    }
}

