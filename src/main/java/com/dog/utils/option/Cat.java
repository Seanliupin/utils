package com.dog.utils.option;

import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Cat<T> {
    private final CompletableFuture<Option<T>> future;

    private Cat(T t) {
        future = CompletableFuture.completedFuture(Option.of(t));
    }

    private Cat(CompletableFuture<Option<T>> future) {
        this.future = future;
    }

    private Cat() {
        future = CompletableFuture.completedFuture(Option.empty());
    }

    private Cat(Option<T> t) {
        future = CompletableFuture.completedFuture(t);
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

    public static <T> Cat<T> of(CompletableFuture<Option<T>> future) {
        return new Cat<>(future);
    }

    public Cat<T> actOnSome(Consumer<T> consumer) {
        CompletableFuture<Option<T>> next = future.thenApply(option -> {
            if (option.hasValue()) {
                try {
                    consumer.accept(option.get());
                } catch (Exception ignored) {
                }
            }
            return option;
        });

        return Cat.of(next);
    }

    private CompletableFuture<Option<T>> getFuture() {
        return future;
    }

    public Cat<T> act(Consumer<Option<T>> consumer) {
        CompletableFuture<Option<T>> next = future.thenApply(option -> {
            try {
                consumer.accept(option);
            } catch (Exception ignored) {
            }
            return option;
        });

        return Cat.of(next);
    }


    public Cat<T> actOnNone(Consumer<Exception> consumer) {
        CompletableFuture<Option<T>> next = future.thenApply(option -> {
            if (option.hasNoValue()) {
                try {
                    consumer.accept(option.error());
                } catch (Exception ignored) {
                }
            }
            return option;
        });

        return Cat.of(next);
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
        CompletableFuture<Option<T>> next = future.thenApply(option -> {
            if (option.hasNoValue()) {
                try {
                    return transformer.apply(option.error());
                } catch (Exception e) {
                    return Option.empty(e);
                }
            }
            return option;
        });
        return Cat.of(next);
    }

    /**
     * 若有值，则映射。
     *
     * @param transformer
     * @param <R>
     * @return
     */
    public <R> Cat<R> someMap(Function<T, Option<R>> transformer) {
        CompletableFuture<Option<R>> next = future.thenApply(option -> {
            if (option.hasValue()) {
                try {
                    return transformer.apply(option.get());
                } catch (Exception e) {
                    return Option.empty(e);
                }
            }
            return Option.empty("");
        });
        return Cat.of(next);
    }


    /**
     * 如果 needFlow为true，则对内容进行同态处理。
     *
     * @param needFlow
     * @param transformer
     * @return
     */
    public Cat<T> someFlowIf(boolean needFlow, Function<T, Option<T>> transformer) {
        CompletableFuture<Option<T>> next = future.thenApply(option -> {
            try {
                if (needFlow) {
                    return option.flatMap(transformer);
                }
                return option;
            } catch (Exception e) {
                return Option.empty(e);
            }
        });
        return Cat.of(next);
    }

    public Cat<T> someFlowIfNot(boolean needFlow, Function<T, Option<T>> transformer) {
        return someFlowIf(!needFlow, transformer);
    }

    public Cat<T> filter(Predicate<T> predicate) {
        CompletableFuture<Option<T>> next = future.thenApply(option -> {
            if (option.hasValue()) {
                try {
                    if (predicate.test(option.get())) {
                        return option;
                    } else {
                        return Option.empty("filter failed");
                    }
                } catch (Exception e) {
                    return Option.empty(e);
                }

            } else {
                return option;
            }
        });
        return Cat.of(next);
    }

    public <R> Cat<R> eitherMap(Function<T, Option<R>> right,
                                Function<Exception, Option<R>> left) {
        CompletableFuture<Option<R>> next = future.thenApply(option -> {
            try {
                if (option.hasValue()) {
                    return option.flatMap(right);
                } else {
                    return left.apply(option.error());
                }
            } catch (Exception e) {
                return Option.empty(e);
            }
        });
        return Cat.of(next);
    }

    public Cat<T> noneFlatMap(Function<Exception, Cat<T>> transformer) {
        CompletableFuture<Option<T>> next = future.thenCompose(option -> {
            try {
                if (option.hasNoValue()) {
                    return transformer.apply(option.error()).getFuture();
                } else {
                    return CompletableFuture.completedFuture(option);
                }
            } catch (Exception e) {
                return CompletableFuture.completedFuture(Option.empty(e));
            }
        });
        return Cat.of(next);
    }

    /**
     * 如果有，则映射。
     *
     * @param transformer
     * @param <R>
     * @return
     */
    public <R> Cat<R> someFlatMap(Function<? super T, Cat<R>> transformer) {
        CompletableFuture<Option<R>> nextStep = future.thenCompose(option -> {
            if (option.hasValue()) {
                Cat<R> re = transformer.apply(option.get());
                return re.getFuture();
            }
            return CompletableFuture.completedFuture(Option.empty(""));
        });

        return Cat.of(nextStep);
    }

    /**
     * 如果 needFlow 为 true，则通过 transformer 对数据进行同态处理。
     *
     * @param needFlow
     * @param transformer
     * @return
     */
    public Cat<T> someFlatFlowIf(boolean needFlow, Function<T, Cat<T>> transformer) {
        CompletableFuture<Option<T>> next = future.thenCompose(option -> {
            try {
                if (needFlow) {
                    return transformer.apply(option.get()).getFuture();
                }
                return CompletableFuture.completedFuture(option);
            } catch (Exception e) {
                return CompletableFuture.completedFuture(Option.empty(e));
            }
        });
        return Cat.of(next);
    }

    public Cat<T> someFlatFlowIfNot(boolean needFlow, Function<T, Cat<T>> transformer) {
        return someFlatFlowIf(!needFlow, transformer);
    }

    /**
     * Go right if there is some value, otherwise go left.
     *
     * @param right
     * @param left
     * @param <R>
     * @return
     */
    public <R> Cat<R> eitherFlatMap(Function<T, Cat<R>> right, Function<Exception, Cat<R>> left) {
        CompletableFuture<Option<R>> next = future.thenCompose(option -> {
            try {
                if (option.hasValue()) {
                    return right.apply(option.get()).getFuture();
                } else {
                    return left.apply(option.error()).getFuture();
                }
            } catch (Exception e) {
                return CompletableFuture.completedFuture(Option.empty(e));
            }
        });
        return Cat.of(next);
    }

    public Mono<Option<T>> getData() {
        return Mono.just("").map(it -> {
            try {
                return future.get();
            } catch (Exception e) {
                return Option.empty(e);
            }
        });
    }

    public Mono<T> getMono() {
        try {
            Option<T> value = future.get();
            return Mono.just(value.get());
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new OptionException(e.getMessage());
            }
        }
    }

    public Option<T> getOption() {
        try {
            return future.get();
        } catch (Exception e) {
            return Option.empty(e);
        }
    }

    public T get() {
        return getOption().get();
    }

    public T get(RuntimeException e) {
        Option<T> option = getOption();
        if (option.hasValue()) {
            return option.get();
        }
        throw e;
    }

    public T getOrElse(T back) {
        Option<T> option = getOption();
        if (option.hasValue()) {
            return option.get();
        }
        return back;
    }
}

