package com.dotgoing.utils.`fun`

import com.alibaba.fastjson.JSONObject
import com.dotgoing.utils.core.None
import com.dotgoing.utils.core.Some

/**
 * 如果没有所需要的key，则抛出异常
 * */
fun JSONObject.requireKey(vararg keys: String): JSONObject {
    val missing = keys.map {
        if (this.containsKey(it)) {
            None<String>()
        } else {
            Some(it)
        }
    }.filter { it.hasValue() }
    if (missing.isNotEmpty()) {
        val v = missing.joinToString { it.value() }
        throw Exception("json obj should contain $v")
    }
    return this
}