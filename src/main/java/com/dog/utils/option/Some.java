package com.dog.utils.option;

import java.util.function.Function;
import java.util.function.Predicate;

public final class Some<T> extends Option<T> {
    private final T t;

    Some(T t) {
        this.t = t;
    }

    @Override
    public Exception error() {
        throw new RuntimeException("This is Some Object");
    }

    @Override
    public <R> Option<R> map(Function<? super T, ? extends R> transformer) {
        try {
            return new Some<>(transformer.apply(get()));
        } catch (Exception e) {
            return Option.empty(e);
        }
    }

    @Override
    public <R> Option<R> flatMap(Function<? super T, ? extends Option<? extends R>> transformer) {
        try {
            return (Option<R>) transformer.apply(get());
        } catch (Exception e) {
            return Option.empty(e);
        }
    }

    @Override
    public Option<T> filter(Predicate<? super T> predicate) {
        try {
            if (predicate.test(get())) {
                return this;
            } else {
                return Option.empty("filter failed");
            }
        } catch (Exception e) {
            return Option.empty(e);
        }
    }

    @Override
    public T get() {
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

