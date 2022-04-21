package com.example.sduitest

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * @author 陈鹏翔
 * Created on 2022/4/20.
 * Desc:
 */
object AssetsUtil {
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getJson(context: Context, fileName: String) = withContext(Dispatchers.IO) {
        val strBuilder = StringBuilder()
        val manager = context.assets
        try {
            val reader = BufferedReader(InputStreamReader(manager.open(fileName), "utf-8"))
            reader.readLines().forEach {
                strBuilder.append(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        strBuilder.toString()
    }
}