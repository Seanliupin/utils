package com.dotgoing.utils.core


fun <T> Option<T>.noneMap(transform: (String) -> T): Option<T> {
    return if (hasNoValue()) {
        try {
            Some(transform(toString()))
        } catch (e: Exception) {
            None<T>(e)
        }
    } else {
        this
    }
}


fun <T> Option<T>.noneMapWithCode(transform: (String, Int) -> T): Option<T> {
    return if (hasNoValue()) {
        try {
            Some(transform(toString(), errorCode()))
        } catch (e: Exception) {
            None<T>(e)
        }
    } else {
        this
    }
}


fun <T> Option<T>.orElse(other: T): T {
    return if (hasNoValue()) {
        other
    } else {
        value()
    }
}


fun <T> Option<T>.getValueOrNull(): T? {
    return if (hasNoValue()) {
        null
    } else {
        value()
    }
}

fun <T> Option<T>.orElseGet(other: () -> T): Option<T> {
    return if (hasNoValue()) {
        try {
            Some(other())
        } catch (e: Exception) {
            None<T>(e)
        }
    } else {
        this
    }
}

fun <T> Option<T>.orOp(other: () -> Option<T>): Option<T> {
    return if (hasValue()) {
        this
    } else {
        try {
            other()
        } catch (e: Exception) {
            None<T>(e)
        }
    }
}


/**
 * TODO：一个设计原则
 * 在Option和Mono的结合中，应当始终遵循 Some应当只负责传递正常数据流，
 * 错误信息通过None来传递，且传递None的时候，尽可能带上错误的描述信息
 * 以给终端用户以clue来定位出错的原因。
 *
 * 在数据流动的每一个节点，都可以对流动的数据进行观察、操作和转换，无论此时
 * 流动的数据是正常数据还是错误信息None。所有的业务逻辑，皆在这些转换当中。
 * 这些转换的函数是数据处理的基本算子。
 * */
fun <T> Option<T>.noneFlatMap(transform: (String) -> Option<T>): Option<T> {
    return if (hasNoValue()) {
        try {
            transform(toString())
        } catch (e: Exception) {
            None<T>(e)
        }

    } else {
        this
    }
}

fun <T> Option<T>.noneFlatMapWithCode(transform: (String, Int) -> Option<T>): Option<T> {
    return if (hasNoValue()) {
        try {
            transform(toString(), errorCode())
        } catch (e: Exception) {
            None<T>(e)
        }

    } else {
        this
    }
}
