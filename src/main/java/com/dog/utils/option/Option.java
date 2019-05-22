package com.dog.utils.option;

import java.util.function.Function;

public abstract class Option<T> {

    public static <T> Option<T> of(T t) {
        return new Some<>(t);
    }

    public static <T> Option<T> empty() {
        return new None<>();
    }

    public static <T> Option<T> empty(String err) {
        return new None<>(new Exception(err));
    }

    public static <T> Option<T> empty(Exception e) {
        return new None<>(e);
    }

    public abstract <R> Option<R> map(Function<? super T, ? extends R> transformer);

    public abstract <R> Option<R> flatMap(Function<? super T, ? extends Option<? extends R>> transformer);

    public abstract T value();

    public abstract Exception error();

    public abstract boolean hasValue();

    public final boolean hasNoValue() {
        return !hasValue();
    }
}

