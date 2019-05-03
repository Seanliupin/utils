package com.dotgoing.utils.core

class None<out T>(val exception: Exception = OptionException()) : Option<T>() {
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

    override fun error(): OptionException {
        if (exception is OptionException) {
            return exception
        }
        return OptionException(exception.message)

    }

    override fun toString(): String {
        return exception.message ?: "none error"
    }
}