package com.dotgoing.utils.cat;


import com.dotgoing.utils.option.Option;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

public class Cat<T> {

    private final Mono<Option<T>> data;

    private Cat(T t) {
        data = Mono.just(Option.of(t));
    }

    private Cat(Mono<Option<T>> d) {
        data = d;
    }

    private Cat() {
        data = Mono.just(Option.empty());
    }

    private Cat(Option<T> t) {
        data = Mono.just(t);
    }

    public static <T> Cat<T> of(T t) {
        return new Cat<>(t);
    }

    public static <T> Cat<T> empty() {
        return new Cat<>();
    }

    public static <T> Mono<Option<T>> mapOf(T t) {

        return Mono.just(Option.of(t));
    }

    public static <T> Mono<Option<T>> mapEmpty() {
        return Mono.just(Option.empty());
    }

    public static <T> Mono<Option<T>> mapEmpty(Exception e) {
        return Mono.just(Option.empty(e));
    }

    public <R> Cat<R> someMap(Function<? super T, ? extends Option<? extends R>> transformer) {
        Mono<Option<R>> mo = data.map((op) -> {
            if (op.hasValue()) {
                try {
                    return op.flatMap(transformer);
                } catch (Exception e) {
                    return Option.empty(e);
                }
            } else {
                return Option.empty(op.error());
            }
        });

        return new Cat<>(mo);
    }

    public Cat<T> actSome(Consumer<T> consumer) {
        Mono<Option<T>> mid = data.map((op) -> {
            if (op.hasValue()) {
                try {
                    consumer.accept(op.value());
                } catch (Exception e) {
                }
            }
            return op;
        });

        return new Cat<>(mid);
    }

    public Cat<T> actNone(Consumer<Exception> consumer) {
        data.map((op) -> {
            if (op.hasNoValue()) {
                try {
                    consumer.accept(op.error());
                } catch (Exception e) {
                }
            }
            return op;
        });

        return new Cat<>(data);
    }

    public Cat<T> noneMap(Function<Exception, Option<T>> transformer) {
        Mono<Option<T>> mo = data.map((op) -> {
            if (op.hasNoValue()) {
                try {
                    return transformer.apply(op.error());
                } catch (Exception e) {
                    return Option.empty(e);
                }
            } else {
                return op;
            }
        });

        return new Cat<>(mo);
    }

    public <R> Cat<R> someFlatMap(Function<? super T, ? extends Mono<? extends Option<R>>> transformer) {
        Mono<Option<R>> mo = data.flatMap((op) -> {
            if (op.hasValue()) {
                try {
                    return transformer.apply(op.value());
                } catch (Exception e) {
                    return Mono.just(Option.empty(e));
                }
            } else {
                return Mono.just(Option.empty());
            }
        });
        return new Cat<>(mo);
    }

    public Cat<T> noneFlatMap(Function<Exception, Mono<Option<T>>> transformer) {
        Mono<Option<T>> mo = data.flatMap((op) -> {
            if (op.hasNoValue()) {
                try {
                    return transformer.apply(op.error());
                } catch (Exception e) {
                    return Mono.just(Option.empty(e));
                }
            } else {
                return Mono.just(op);
            }
        });
        return new Cat<>(mo);
    }

    public Option<T> block() {
        return data.block();
    }

    public Option<T> value() {
        return data.block();
    }

    public Mono<Option<T>> getData() {
        return data;
    }

    public T getOrElse(T back) {
        Option<T> option = data.block();
        if (option != null && option.hasValue()) {
            return option.value();
        }
        return back;
    }
}

