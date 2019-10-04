package com.dog.utils.option;


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
        if (t == null) {
            return empty(new CatInitException("can not init cat by null value"));
        }
        return new Cat<>(t);
    }

    public static <T> Cat<T> of(Option<T> t) {
        return new Cat<>(t);
    }

    public static <T> Cat<T> empty() {
        return new Cat<>();
    }

    public static <T> Cat<T> empty(String err) {
        return new Cat<>(Option.empty(err));
    }

    public static <T> Cat<T> empty(Exception e) {
        return new Cat<>(Option.empty(e));
    }


    public static <T> Option<T> mapOf(T t) {
        return new Some<>(t);
    }

    public static <T> Option<T> mapEmpty() {
        return new None<>();
    }

    public static <T> Option<T> mapEmpty(String err) {
        return new None<>(new Exception(err));
    }

    public static <T> Option<T> mapEmpty(Exception e) {
        return new None<>(e);
    }

    public static <T> Mono<Option<T>> flatMapOf(T t) {
        return Mono.just(Option.of(t));
    }

    public static <T> Mono<Option<T>> flatMapOf(Option<T> t) {
        return Mono.just(t);
    }

    public static <T> Mono<Option<T>> flatMapEmpty() {
        return Mono.just(Option.empty());
    }

    public static <T> Mono<Option<T>> flatMapEmpty(String err) {
        return Mono.just(Option.empty(err));
    }

    public static <T> Mono<Option<T>> flatMapEmpty(Exception e) {
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

    public Cat<T> actOnSome(Consumer<T> consumer) {
        Mono<Option<T>> mid = data.map((op) -> {
            if (op.hasValue()) {
                try {
                    consumer.accept(op.get());
                } catch (Exception e) {
                }
            }
            return op;
        });

        return new Cat<>(mid);
    }

    public Cat<T> act(Consumer<Option<T>> consumer) {
        Mono<Option<T>> mid = data.map((op) -> {
            try {
                consumer.accept(op);
            } catch (Exception e) {
            }
            return op;
        });

        return new Cat<>(mid);
    }


    public Cat<T> actOnNone(Consumer<Exception> consumer) {
        Mono<Option<T>> mid = data.map((op) -> {
            if (op.hasNoValue()) {
                try {
                    consumer.accept(op.error());
                } catch (Exception e) {
                }
            }
            return op;
        });

        return new Cat<>(mid);
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

    public <R> Cat<R> someFlatMap(Function<? super T, Cat<R>> transformer) {
        Mono<Option<R>> mo = data.flatMap((op) -> {
            if (op.hasValue()) {
                try {
                    return transformer.apply(op.get()).getData();
                } catch (Exception e) {
                    return Mono.just(Option.empty(e));
                }
            } else {
                return Mono.just(Option.empty(op.error()));
            }
        });
        return new Cat<>(mo);
    }

    public Cat<T> noneFlatMap(Function<Exception, Cat<T>> transformer) {
        Mono<Option<T>> mo = data.flatMap((op) -> {
            if (op.hasNoValue()) {
                try {
                    return transformer.apply(op.error()).getData();
                } catch (Exception e) {
                    return Mono.just(Option.empty(e));
                }
            } else {
                return Mono.just(op);
            }
        });
        return new Cat<>(mo);
    }

    public Mono<Option<T>> getData() {
        return data;
    }

    public Option<T> getOption() {
        return data.block();
    }

    public T get() {
        Option<T> option = data.block();
        assert option != null;
        if (option.hasValue()) {
            return option.get();
        }

        if (option.error() instanceof RuntimeException) {
            throw (RuntimeException) option.error();
        }

        throw new RuntimeException(option.error());
    }

    public T get(RuntimeException e) {
        Option<T> option = data.block();
        assert option != null;
        if (option.hasValue()) {
            return option.get();
        }

        throw e;
    }

    public T getOrElse(T back) {
        Option<T> option = data.block();
        assert option != null;
        if (option.hasValue()) {
            return option.get();
        }
        return back;
    }
}

