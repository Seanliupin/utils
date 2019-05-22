package com.dotgoing.utils.cat;


import com.dotgoing.utils.option.None;
import com.dotgoing.utils.option.Option;
import com.dotgoing.utils.option.Some;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class Cat<T> {

    private final Mono<Option<T>> data;

    private Cat(T t) {
        data = Mono.just(new Some<>(t));
    }

    private Cat(Mono<Option<T>> d) {
        data = d;
    }

    private Cat() {
        data = Mono.just(new None<>());
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
                try {
                    return op.flatMap(transformer);
                } catch (Exception e) {
                    return new None<>(e);
                }
            } else {
                return new None<>();
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
                    return Mono.just(new None<>(e));
                }
            } else {
                return Mono.just(new None<>());
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

    public T getOrElse(T back) {
        Option<T> option = data.block();
        if (option != null && option.hasValue()) {
            return option.value();
        }
        return back;
    }
}

