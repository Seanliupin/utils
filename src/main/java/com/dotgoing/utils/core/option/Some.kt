package com.dotgoing.utils.core.option

class Some<out T>(private val t: T) : Option<T>() {
    override fun hasValue(): Boolean {
        return true
    }

    override fun value(): T {
        return t
    }

    override fun errorCode(): Int {
        throw Exception("this data is ok: ${this}")
    }

    override fun error(): Exception {
        throw OptionStateException("Some obj has no error : ${this}")
    }

    override fun toString(): String {
        return try {
            t as String
        } catch (e: Exception) {
            "Some($t)"
        }

    }
}