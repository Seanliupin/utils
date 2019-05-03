package com.dotgoing.utils.core

class None<out T>(val exception: OptionException = OptionException()) : Option<T>() {
    override fun hasValue(): Boolean {
        return false
    }

    override fun value(): T {
        throw OptionStateException("none obj has no value : ${this}")
    }

    override fun errorCode(): Int {
        return exception.code
    }

    override fun error(): OptionException {
        return exception
    }

    override fun toString(): String {
        return exception.message ?: "none error"
    }
}