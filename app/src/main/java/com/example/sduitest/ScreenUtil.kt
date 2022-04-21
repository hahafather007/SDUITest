package com.example.sduitest

import android.content.Context
import kotlin.math.roundToInt

/**
 * @author 陈鹏翔
 * Created on 2022/4/20.
 * Desc:
 */
object ScreenUtil {
    private const val DEFAULT_WIDTH = 375f

    private var mDensity = 0f

    fun init(context: Context) {
        val dm = context.resources.displayMetrics
        val width = dm.widthPixels
        mDensity = width / DEFAULT_WIDTH
    }

    fun dp2px(dp: Int): Int {
        return (dp * mDensity).roundToInt()
    }

    fun dp2px(dp: Float): Int {
        return (dp * mDensity).roundToInt()
    }

    fun dp2px(dp: Double): Int {
        return (dp * mDensity).roundToInt()
    }
}

val Number.dp: Int
    get() {
        return when (this) {
            is Int -> ScreenUtil.dp2px(this)
            is Float -> ScreenUtil.dp2px(this)
            is Double -> ScreenUtil.dp2px(this)
            else -> 0
        }
    }