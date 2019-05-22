package com.dotgoing.utils.j.option;


import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;

import java.util.function.Function;

public class OrMono<T> extends Mono<Option<T>> {

    private final Mono<Option<T>> data;

    public OrMono(T t) {
        data = Mono.just(new Some(t));
    }

    public OrMono(Option<T> t) {
        data = Mono.just(t);
    }

    public static <T> OrMono<T> my(T t) {
        return new OrMono(t);
    }
//
//    public <R> OrMono<R> someMap(Function<? super T, ? extends Option<? extends R>> transformer) {
//         data.map((op) -> {
//            if (op.hasValue()) {
//                return op.flatMap(transformer);
//            } else {
//                return new None<>();
//            }
//        });
//    }

    public <R> Mono<Option<R>> someFlatMap(Function<? super T, ? extends Mono<? extends Option<R>>> transformer) {
        return flatMap((op) -> {
            if (op.hasValue()) {
                Mono<? extends Option<R>> s = transformer.apply(op.value());
                return s;
            } else {
                return Mono.just(new None<R>());
            }
        });
    }


    @Override
    public void subscribe(CoreSubscriber<? super Option<T>> actual) {
        Operators.complete(actual);
    }
}

