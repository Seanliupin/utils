package com.dotgoing.utils.core.option

class None<out T>(val exception: Exception = OptionException()) : Option<T>() {

    constructor(string: String) : this(OptionException(string))

    override fun hasValue(): Boolean {
        return false
    }

    override fun value(): T {
        throw OptionStateException("none obj has no value : ${this}")
    }

    override fun errorCode(): Int {
        if (exception is OptionException) {
            return exception.code
        }
        val default = OptionException()
        return default.code
    }

    override fun error(): Exception {
        return exception

    }

    override fun toString(): String {
        return exception.message ?: "none error"
    }
}