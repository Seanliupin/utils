package com.dotgoing.utils.core.json;

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import java.io.Serializable

abstract class JSONData : Serializable {
    abstract fun toJSON(): JSONObject

    fun safeToJSON(data: String): JSONObject {
        return try {
            JSON.parseObject(data)
        } catch (e: Exception) {
            JSONObject()
        }
    }

    fun safeToRich(data: String): JSONArray {
        return try {
            JSON.parseArray(data)
        } catch (e: Exception) {
            JSONArray()
        }
    }
}