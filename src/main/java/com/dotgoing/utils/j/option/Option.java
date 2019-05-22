package com.dotgoing.utils.j.option;

import java.util.function.Function;

public abstract class Option<T> {

    public abstract <R> Option<R> map(Function<? super T, ? extends R> transformer);

    public abstract <R> Option<R> flatMap(Function<? super T, ? extends Option<? extends R>> transformer);

    public abstract T value();

    public abstract boolean hasValue();
}

