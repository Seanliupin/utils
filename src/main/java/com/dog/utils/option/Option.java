package com.dog.utils.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Option<T> {

    public static <T> Option<T> of(T t) {
        return (t == null) ? empty() : new Some<>(t);
    }

    public static <T> Option<T> empty() {
        return new None<>();
    }

    public static <T> Option<T> empty(String err) {
        return new None<>(err);
    }

    public static <T> Option<T> empty(Exception e) {
        return new None<>(e);
    }

    public abstract <R> Option<R> map(Function<? super T, ? extends R> transformer);

    public abstract Option<T> filter(Predicate<? super T> predicate);

    public void forEach(Consumer<T> consumer) {
        if (hasValue()) {
            consumer.accept(get());
        }
    }

    public void consume(Consumer<T> consumer) {
        if (hasValue()) {
            consumer.accept(get());
        }
    }

    public abstract <R> Option<R> flatMap(Function<? super T, ? extends Option<? extends R>> transformer);

    public abstract T get();

    public T getOrThrow(String msg) {
        if (hasValue()) {
            return get();
        } else {
            throw new OptionException(msg);
        }
    }

    public T getOrElse(T back) {
        if (hasValue()) {
            return get();
        } else {
            return back;
        }
    }

    public T getOrElseLazy(Supplier<T> back) {
        if (hasValue()) {
            return get();
        } else {
            return back.get();
        }
    }

    public Option<T> makeSure(Predicate<T> predicate) {
        if (hasValue() && !predicate.test(get())) {
            return Option.empty();
        }
        return this;
    }

    public abstract Exception error();

    public abstract boolean hasValue();

    public final boolean hasNoValue() {
        return !hasValue();
    }
}

