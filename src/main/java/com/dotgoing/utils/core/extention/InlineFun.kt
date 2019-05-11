package com.dotgoing.utils.core.extention

import com.dotgoing.utils.core.option.None
import com.dotgoing.utils.core.option.Option
import com.dotgoing.utils.core.option.Some
import reactor.core.publisher.Mono

fun <S> monoOp(obj: S?, msg: String? = null): Mono<Option<S>> {
    return Mono.just(op(obj, msg))
}

fun <S> monoOp(op: Option<S>): Mono<Option<S>> {
    return Mono.just(op)
}

fun <S> op(obj: S?, msg: String? = null): Option<S> {
    return if (obj == null) {
        None(Exception(msg ?: "none value construct by op function in MonoOpHelper"))
    } else {
        Some(obj)
    }
}