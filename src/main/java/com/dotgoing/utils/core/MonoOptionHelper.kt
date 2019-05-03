package com.dotgoing.utils.core

import reactor.core.publisher.Mono

/**
 * 只有有值的时候才映射
 * */
fun <R, T> Mono<Option<R>>.someMap(transform: (R) -> Option<T>): Mono<Option<T>> {
    return map {
        it.flatMap(transform)
    }
}

fun <T> Mono<Option<T>>.orValue(other: (String) -> T): Mono<Option<T>> {
    return map {
        if (it.hasValue()) {
            it
        } else {
            try {
                Some(other(it.toString()))
            } catch (e: Exception) {
                None<T>(e)
            }
        }
    }
}

fun <T> Mono<Option<T>>.orMono(other: (Option<T>) -> Mono<Option<T>>): Mono<Option<T>> {
    return flatMap {
        if (it.hasValue()) {
            Mono.just(it)
        } else {
            try {
                other(it)
            } catch (e: Exception) {
                Mono.just(None<T>(e))
            }
        }
    }
}

fun <T> Mono<Option<T>>.orOption(other: (Option<T>) -> Option<T>): Mono<Option<T>> {
    return flatMap {
        if (it.hasValue()) {
            this
        } else {
            try {
                Mono.just(other(it))
            } catch (e: Exception) {
                Mono.just(None<T>(e))
            }
        }
    }
}

fun <T> Mono<Option<T>>.mapValue(other: (T) -> T): Mono<Option<T>> {
    return someMap {
        try {
            Some(other(it))
        } catch (e: Exception) {
            None<T>(e)
        }
    }
}
