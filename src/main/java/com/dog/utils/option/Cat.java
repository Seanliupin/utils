package com.dog.utils.option;


import reactor.core.publisher.Mono;
import reactor.core.scheduler.NonBlocking;
import reactor.core.scheduler.Scheduler;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Cat<T> {

    private static ExecutorService service = Executors.newFixedThreadPool(3);
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

    public static void setService(ExecutorService newService) {
        if (newService == null) {
            throw new NullPointerException("server should not be null");
        }
        service = newService;
    }

    public static <T> Cat<T> of(T t) {
        if (t == null) {
            return empty(new CatInitException("can not init cat by null value"));
        }
        return new Cat<>(t);
    }

    public static <T> Cat<T> of(Optional<T> t) {
        if (Objects.isNull(t)) {
            return empty();
        }
        return t.map(Cat::of).orElseGet(Cat::empty);
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

    public final Cat<T> publishOn(Scheduler scheduler) {
        return new Cat<>(data.publishOn(scheduler));
    }

    public final Cat<T> subscribeOn(Scheduler scheduler) {
        return new Cat<>(data.subscribeOn(scheduler));
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

    /**
     * 如果没有值，通过 noneMap 可以补救一下，以返回一个具体值。
     * 也可以返回一个None，带上一个语义更加清晰的异常，以让调用者捕获该异常
     * 也可以直接抛出一个异常，则该异常会被包装到None对象中，以最终传递给调用者
     *
     * @param transformer
     * @return
     */
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

    /**
     * 若有值，则映射。
     *
     * @param transformer
     * @param <R>
     * @return
     */
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

    public Cat<T> filter(Predicate<? super T> predicate) {
        Mono<Option<T>> mo = data.map((op) -> {
            if (op.hasValue()) {
                if (predicate.test(op.get())) {
                    return op;
                } else {
                    return Option.empty("filter failed");
                }
            } else {
                return Option.empty(op.error());
            }
        });

        return new Cat<>(mo);
    }

    public <R> Cat<R> eitherMap(Function<? super T, ? extends Option<? extends R>> right,
                                Function<Exception, Option<R>> left) {
        Mono<Option<R>> mo = data.map((op) -> {
            if (op.hasValue()) {
                try {
                    return op.flatMap(right);
                } catch (Exception e) {
                    return Option.empty(e);
                }
            } else {
                try {
                    return left.apply(op.error());
                } catch (Exception e) {
                    return Option.empty(e);
                }
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

    /**
     * Go right if there is some value, otherwise go left.
     *
     * @param right
     * @param left
     * @param <R>
     * @return
     */
    public <R> Cat<R> eitherFlatMap(Function<? super T, Cat<R>> right, Function<Exception, Cat<R>> left) {
        Mono<Option<R>> mo = data.flatMap((op) -> {
            if (op.hasValue()) {
                try {
                    return right.apply(op.get()).getData();
                } catch (Exception e) {
                    return Mono.just(Option.empty(e));
                }
            } else {
                try {
                    return left.apply(op.error()).getData();
                } catch (Exception e) {
                    return Mono.just(Option.empty(e));
                }
            }
        });
        return new Cat<>(mo);
    }

    public Mono<Option<T>> getData() {
        return data;
    }

    public Mono<T> getMono() {
        return data.map((a) -> {
            if (a.hasValue()) {
                return a.get();
            } else {
                if (a.error() instanceof RuntimeException) {
                    throw (RuntimeException) a.error();
                } else {
                    throw new OptionException(a.error());
                }
            }
        });
    }

    public Option<T> getOption() {
        return data.block();
    }

    public T get() {
        Option<T> option;
        try {
            if (Thread.currentThread() instanceof NonBlocking) {
                Future<Option<T>> future = service.submit((Callable<Option<T>>) data::block);
                option = future.get();
            } else {
                option = data.block();
            }
            assert option != null;
            if (option.hasValue()) {
                return option.get();
            }
        } catch (Exception e) {
            option = None.empty(new OptionException(e));
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

