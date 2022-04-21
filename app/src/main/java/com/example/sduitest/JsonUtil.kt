package com.example.sduitest

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * @author 陈鹏翔
 * Created on 2022/4/20.
 * Desc:
 */
fun JSONObject.getIntOrNull(name: String): Int? {
    return try {
        getInt(name)
    } catch (ignore: JSONException) {
        null
    }
}

fun JSONObject.getStringOrNull(name: String): String? {
    return try {
        getString(name)
    } catch (ignore: JSONException) {
        null
    }
}

fun JSONObject.getArrayOrNull(name: String): JSONArray? {
    return try {
        get(name) as JSONArray
    } catch (ignore: JSONException) {
        null
    }
}

fun JSONObject.getObjectOrNull(name: String): JSONObject? {
    return try {
        get(name) as JSONObject
    } catch (ignore: JSONException) {
        null
    }
}

fun JSONObject.getOrNull(name: String): Any? {
    return try {
        get(name)
    } catch (ignore: JSONException) {
        null
    }
}

fun JSONArray.getObjectOrNull(index: Int): JSONObject? {
    return try {
        get(index) as JSONObject
    } catch (ignore: JSONException) {
        null
    }
}