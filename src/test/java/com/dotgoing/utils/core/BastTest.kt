package com.dotgoing.utils.core

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject

abstract class BastTest() {

    protected fun Option<JSONObject>.shouldHasNotKey(key: String, msg: String = ""): Option<JSONObject> {
        shouldHasValue()

        value().shouldHasNotKey(key, msg)
        return this
    }

    protected fun <T> Option<T>.shouldHasValue(): Option<T> {
        if (hasNoValue()) {
            bug("hasNoValue error. $this")
        }

        return this
    }

    protected fun JSONObject.shouldHasNotKey(key: String, msg: String = ""): JSONObject {
        if (containsKey(key)) {
            bug("key $key should not exist in response: $this")
        }

        return this
    }


    fun <T> Option<T>.shouldEqual(t: Option<T>, msg: String? = null) {
        if (this != t) {
            bug(msg ?: "(${this}) should equal ($t)")
        }
    }

    fun <T> Option<T>.shouldNotEqual(t: Option<T>, msg: String? = null) {
        if (this == t) {
            bug(msg ?: "(${this}) should not equal ($t)")
        }
    }

    protected fun bug(msg: String) {
        assert(false, { msg })
    }

}

