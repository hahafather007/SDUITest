package com.example.sduitest

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ScreenUtil.init(this)
        initSDUI()
    }

    private fun initSDUI() {
        lifecycleScope.launch(Dispatchers.Default) {
            var str = AssetsUtil.getJson(this@MainActivity, "layout.json")
            var obj = JSONObject(str)
            val root = dealWithUi(findViewById(R.id.root_view), obj)

            str = AssetsUtil.getJson(this@MainActivity, "data.json")
            obj = JSONObject(str)
            dealWithData(root, obj)
        }
    }

    /**
     * 根据layout.json来解析UI
     */
    private suspend fun dealWithUi(parent: ViewGroup, jsonObj: JSONObject): View {
        val view = when (jsonObj.getString("type")) {
            "FrameLayout" -> {
                FrameLayout(this).also {
                    setBaseAttr(it, parent, jsonObj)
                }
            }
            "ImageView" -> {
                AppCompatImageView(this).also {
                    setBaseAttr(it, parent, jsonObj)
                }
            }
            "TextView" -> {
                AppCompatTextView(this).also {
                    setBaseAttr(it, parent, jsonObj)
                    getGravity(jsonObj)?.let { gravity ->
                        it.gravity = gravity
                    }
                }
            }
            "LinearLayout" -> {
                LinearLayout(this).also {
                    setBaseAttr(it, parent, jsonObj)
                    getGravity(jsonObj)?.let { gravity ->
                        it.gravity = gravity
                    }
                    getOrientation(jsonObj)?.let { orientation ->
                        it.orientation = orientation
                    }
                }
            }
            else -> {
                return View(this)
            }
        }

        // ui操作需要切换到主线程
        withContext(Dispatchers.Main) {
            parent.addView(view)
        }

        // 遍历循环子元素
        jsonObj.getArrayOrNull("children")?.let { children ->
            // RecyclerView需要特殊处理
            if (view is RecyclerView) {

            } else {
                (0 until children.length()).mapNotNull {
                    children.getObjectOrNull(it)
                }.forEach {
                    dealWithUi(view as ViewGroup, it)
                }
            }
        }

        // 返回当前的view，以便于外部可能会做操作
        return view
    }

    /**
     * 设置基础属性，如LayoutParams和background等
     */
    private fun setBaseAttr(view: View, parent: ViewGroup, jsonObj: JSONObject) {
        jsonObj.getIntOrNull("id")?.let {
            view.id = it
        }
        view.tag = jsonObj.getStringOrNull("tag")

        val width = jsonObj.getIntOrNull("width")?.let {
            if (it > 0) {
                it.dp
            } else {
                it
            }
        }
        val height = jsonObj.getIntOrNull("height")?.let {
            if (it > 0) {
                it.dp
            } else {
                it
            }
        }
        var gravity: Int? = null
        var left: Int? = null
        var top: Int? = null
        var right: Int? = null
        var bottom: Int? = null
        jsonObj.getObjectOrNull("position")?.let { position ->
            gravity = getGravity(position)

            left = position.getIntOrNull("left")?.dp
            top = position.getIntOrNull("top")?.dp
            right = position.getIntOrNull("right")?.dp
            bottom = position.getIntOrNull("bottom")?.dp
        }

        when (parent) {
            is FrameLayout -> {
                view.layoutParams = FrameLayout.LayoutParams(width ?: 0, height ?: 0).apply {
                    gravity?.let {
                        this.gravity = it
                    }
                    setMargins(left ?: 0, top ?: 0, right ?: 0, bottom ?: 0)
                }
            }
            is ConstraintLayout -> {
                view.layoutParams = ConstraintLayout.LayoutParams(width ?: 0, height ?: 0).apply {
                    setMargins(left ?: 0, top ?: 0, right ?: 0, bottom ?: 0)
                }
            }
            is LinearLayout -> {
                view.layoutParams = LinearLayout.LayoutParams(width ?: 0, height ?: 0).apply {
                    setMargins(left ?: 0, top ?: 0, right ?: 0, bottom ?: 0)
                }
            }
        }

        jsonObj.getObjectOrNull("background")?.let { background ->
            background.getStringOrNull("color")?.let { color ->
                try {
                    view.setBackgroundColor(Color.parseColor(color))
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getGravity(jsonObj: JSONObject): Int? {
        return when (jsonObj.getStringOrNull("gravity")) {
            "center" -> Gravity.CENTER
            "bottom" -> Gravity.BOTTOM
            else -> null
        }
    }

    private fun getOrientation(jsonObj: JSONObject): Int? {
        return when (jsonObj.getStringOrNull("orientation")) {
            "vertical" -> LinearLayoutCompat.VERTICAL
            "horizontal" -> LinearLayoutCompat.HORIZONTAL
            else -> null
        }
    }

    /**
     * 解析数据并设置到view上
     */
    private suspend fun dealWithData(view: View, jsonObj: JSONObject) {
        val tag = view.tag as String
        val value = jsonObj.getOrNull(tag)

        when (view) {
            is ImageView -> {
                if (value is String) {
                    withContext(Dispatchers.Main) {
                        Glide.with(view).load(value).into(view)
                    }
                }
            }
            is TextView -> {
                if (value is String) {
                    withContext(Dispatchers.Main) {
                        view.text = value
                    }
                }
            }
            is RecyclerView -> {

            }
            else -> {
                if (view is ViewGroup && value is JSONObject) {
                    view.children.forEach {
                        dealWithData(it, value)
                    }
                }
            }
        }
    }
}