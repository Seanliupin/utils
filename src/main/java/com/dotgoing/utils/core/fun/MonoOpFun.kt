package com.dotgoing.utils.core.`fun`

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.dotgoing.utils.core.json.JSONData
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

fun <T> Mono<Option<T>>.orMono(other: (Option<T>) -> Mono<Option<T>>): Mono<Option<T>> {
    return flatMap { it ->
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
    return flatMap { it ->
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

fun <T> Mono<Option<T>>.orValue(other: (String) -> T): Mono<Option<T>> {
    return map { it ->
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

fun <T> Mono<Option<T>>.mapValue(other: (T) -> T): Mono<Option<T>> {
    return someMap { it ->
        try {
            Some(other(it))
        } catch (e: Exception) {
            None<T>(e)
        }
    }
}

/**
 * 当满足条件时，继续。否则传None. 这是应该用filter来代替
 * */
fun <T> Mono<Option<T>>.valueFilter(msg: String? = null, op: (T) -> Boolean): Mono<Option<T>> {
    return someMap { it ->
        try {
            if (op(it)) {
                Some(it)
            } else {
                None<T>(Exception(msg
                        ?: "when process mono option, some condition not satisfied"))
            }
        } catch (e: Exception) {
            None<T>(e)
        }
    }
}

/**
 * 当满足条件时，继续。否则传None
 * */
fun <T> Mono<Option<T>>.valueFilterNot(msg: String? = null, op: (T) -> Boolean): Mono<Option<T>> {
    return valueFilter(msg) { !op(it) }
}


fun Mono<Option<JSONObject>>.requireKey(vararg keys: String): Mono<Option<JSONObject>> {
    return someMap { json ->
        try {
            json.requireKey(*keys)
            Some(json)
        } catch (e: Exception) {
            None<JSONObject>(e)
        }
    }
}


/**
 * 只有有值的时候才映射
 * */
fun <R, T> Mono<Option<R>>.someMap(transform: (R) -> Option<T>): Mono<Option<T>> {
    return map { it ->
        it.flatMap(transform)
    }
}

/**
 * 该算子就相当于在该节点进行错误信息处理。通常，可根据错误的Message决定是转换错误信息还是给出新值以
 * 让程序往下走。
 * */
fun <R> Mono<Option<R>>.noneMap(transform: (String) -> Option<R>): Mono<Option<R>> {
    return map { it ->
        it.noneFlatMap(transform)
    }
}


/**
 * 这里构造了一个分支结构。提供了有值或没有值两个分支，调用者需要给出两种情况下的返回值。
 * */
fun <R, T> Mono<Option<R>>.updateMap(exist: (R) -> Option<T>, none: (String) -> Option<T>): Mono<Option<T>> {
    return map { it ->
        try {
            if (it.hasValue()) {
                exist(it.value())
            } else {
                none(it.toString())
            }
        } catch (e: Exception) {
            None<T>(e)
        }
    }
}


fun <R, T> Mono<Option<R>>.someFlatMap(transform: (R) -> Mono<Option<T>>): Mono<Option<T>> {
    return flatMap { it ->
        if (it.hasValue()) {
            try {
                transform(it.value())
            } catch (e: Exception) {
                Mono.just(None<T>(e))
            }

        } else {
            Mono.just(None(it.error()))
        }
    }
}

/**
 * 当没有值的时候才映射
 * */
fun <R> Mono<Option<R>>.noneFlatMap(transform: (String) -> Mono<Option<R>>): Mono<Option<R>> {
    return flatMap { it ->
        if (it.hasNoValue()) {
            try {
                transform(it.toString())
            } catch (e: Exception) {
                Mono.just(None<R>(e))
            }
        } else {
            Mono.just(it)
        }
    }
}

fun <R> Mono<Option<R>>.noneFlatMapWithCode(transform: (String, Int) -> Mono<Option<R>>): Mono<Option<R>> {
    return flatMap { it ->
        if (it.hasNoValue()) {
            try {
                transform(it.toString(), it.errorCode())
            } catch (e: Exception) {
                Mono.just(None<R>(e))
            }
        } else {
            Mono.just(it)
        }
    }
}

fun <R, T> Mono<Option<R>>.updateFlatMap(exist: (R) -> Mono<Option<T>>, none: (String) -> Mono<Option<T>>): Mono<Option<T>> {
    return flatMap { it ->
        try {
            if (it.hasValue()) {
                exist(it.value())
            } else {
                none(it.toString())
            }
        } catch (e: Exception) {
            Mono.just(None<T>(e))
        }
    }
}

private fun <R> okJSON(vararg pairs: Pair<String, R>): JSONObject {
    val json = JSONObject()
    pairs.forEach { (key, value) ->
        try {
            value as JSONData
            json.put(key, value.toJSON())
        } catch (e: Exception) {
            try {
                value as Iterable<*>
                val arr = JSONArray()
                value.forEach {
                    it as JSONData
                    arr[-1] = it.toJSON()
                }

                json.put(key, arr)
            } catch (e: Exception) {
                json.put(key, value)
            }
        }
    }
    return json
}

private fun <R> okData(data: R): JSONObject {
    return try {
        val d = data as Iterable<*>
        okJSON("data" to d.toList())
    } catch (e: Exception) {
        okJSON("data" to data)
    }
}

/**
 * return response if response is not null
 * if there is something, then return data: something
 * if there is nothing, then return error message and its error code
 * */
fun <R> Mono<Option<R>>.end(respond: String? = null): Mono<String> {
    return map { it ->
        respond ?: if (it.hasValue()) {
            okData(it.value()).toString()
        } else {
            val json = JSONObject()
            json.put("errcode", it.errorCode())
            json.put("errmsg", it.toString())
            json.toString()
        }
    }
}

/**
 * 在最终返回字符串之前，可以做一点收尾工作。比如将map的一些数据设置进去，以在页面上显示等。
 * 这样的设计，有点像finally。无论数据流是成功或是失败，都会执行这个action。
 * */
fun <R> Mono<Option<R>>.endAction(respond: String? = null, action: (Option<R>) -> Any): Mono<String> {
    return map { it ->
        action(it)
        respond ?: if (it.hasValue()) {
            "${it.value()}"
        } else {
            "$it"
        }
    }
}

/**
 * 无论有没有值都会调用action
 * */
fun <R> Mono<Option<R>>.act(action: (Option<R>) -> Unit): Mono<Option<R>> {
    return map { it.act(action) }
}

/**
 * 当有值的时候才会调用action
 * */
fun <R> Mono<Option<R>>.actOnSome(action: (R) -> Unit): Mono<Option<R>> {
    return map { it.actOnSome(action) }
}

fun <R> Mono<Option<R>>.actOnNone(action: (String) -> Unit): Mono<Option<R>> {
    return map { it.actOnNone(action) }
}