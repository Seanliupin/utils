package com.dotgoing.utils.j.option;


import reactor.core.publisher.Mono;

import java.util.function.Function;

public class Cat<T> {

    private final Mono<Option<T>> data;

    private Cat(T t) {
        data = Mono.just(new Some(t));
    }

    private Cat(Mono<Option<T>> d) {
        data = d;
    }

    private Cat() {
        data = Mono.just(new None());
    }

    public Cat(Option<T> t) {
        data = Mono.just(t);
    }


    public static <T> Cat<T> of(T t) {
        return new Cat<>(t);
    }

    public static <T> Cat<T> empty() {
        return new Cat<>();
    }

    public <R> Cat<R> someMap(Function<? super T, ? extends Option<? extends R>> transformer) {
        Mono<Option<R>> mo = data.map((op) -> {
            if (op.hasValue()) {
                return op.flatMap(transformer);
            } else {
                return new None<>();
            }
        });

        return new Cat(mo);
    }

    public <R> Cat<R> someFlatMap(Function<? super T, ? extends Mono<? extends Option<R>>> transformer) {
        Mono<Option<R>> mo = data.flatMap((op) -> {
            if (op.hasValue()) {
                Mono<? extends Option<R>> s = transformer.apply(op.value());
                return s;
            } else {
                return Mono.just(new None<>());
            }
        });
        return new Cat(mo);
    }

    public Option<T> block() {
        return data.block();
    }

    public T getOrElse(T back) {
        Option<T> option = data.block();
        if (option.hasValue()) {
            return option.value();
        }
        return back;
    }
}

