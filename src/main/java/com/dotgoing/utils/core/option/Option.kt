package com.dotgoing.utils.core.option

abstract class Option<out T> {
    fun hasNoValue(): Boolean {
        return !hasValue()
    }

    abstract fun hasValue(): Boolean

    abstract fun errorCode(): Int

    abstract fun error(): Exception

    abstract fun value(): T

    fun <R> map(transform: (T) -> R): Option<R> {
        return try {
            if (hasValue()) {
                Some(transform(value()))
            } else {
                None(error())
            }
        } catch (e: Exception) {
            None(OptionException(e))
        }
    }

    fun valueFilter(msg: String? = null, op: (T) -> Boolean): Option<T> {
        return if (hasValue()) {
            try {
                if (op(value())) {
                    return this
                } else {
                    None<T>(OptionException(msg ?: "condition not satisfied"))
                }
            } catch (e: Exception) {
                None<T>(OptionException(e))
            }
        } else {
            return this
        }
    }

    fun valueFilterNot(msg: String? = null, op: (T) -> Boolean): Option<T> {
        return valueFilter(msg, { !op(it) })
    }

    fun act(action: (Option<T>) -> Unit): Option<T> {
        try {
            action(this)
        } catch (e: Exception) {
            None<T>(OptionException(e))
        }

        return this
    }

    fun actOnSome(action: (T) -> Unit): Option<T> {
        if (hasValue()) {
            try {
                action(value())
            } catch (e: Exception) {
            }

        }
        return this
    }

    fun actOnNone(action: (String) -> Unit): Option<T> {
        if (hasNoValue()) {
            try {
                action(toString())
            } catch (e: Exception) {
            }
        }
        return this
    }

    fun <R> flatMap(transform: (T) -> Option<R>): Option<R> {
        return if (hasValue()) {
            try {
                transform(value())
            } catch (e: Exception) {
                None<R>(e)
            }
        } else {
            None(error())
        }
    }

    override fun equals(other: Any?): Boolean {
        return try {
            val v = other as Option<T>
            if (hasValue() && v.hasValue()) {
                return value() == v.value()
            }

            if (hasValue() && v.hasNoValue()) {
                return false
            }

            if (hasNoValue() && v.hasValue()) {
                return false
            }

            //此时二者都没有值。则比较错误的内容
            if (v.error().javaClass.name != error().javaClass.name) {
                return false
            }
            return v.toString() == toString()
        } catch (e: Exception) {
            false
        }
    }

    override fun hashCode(): Int {
        return if (hasValue()) {
            value()!!.hashCode()
        } else {
            toString().hashCode()
        }
    }
}