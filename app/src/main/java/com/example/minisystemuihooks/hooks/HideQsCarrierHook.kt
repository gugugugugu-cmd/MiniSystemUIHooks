package com.example.minisystemuihooks.hooks

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.minisystemuihooks.HookEntry
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object HideQsCarrierHook {

    private const val CUSTOM_TEXT = "MiniSystemUIHooks"
    private const val CUSTOM_TAG = "mini_custom_qs_text"

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        hookShadeHeaderController(lpparam)
    }

    private fun hookShadeHeaderController(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.systemui.shade.ShadeHeaderController"

        try {
            val clazz = XposedHelpers.findClassIfExists(className, lpparam.classLoader)
            if (clazz == null) {
                HookEntry.log("$className not found")
                return
            }

            XposedHelpers.findAndHookMethod(
                clazz,
                "onInit",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        HookEntry.log("$className.onInit")
                        replaceBySiblingText(param.thisObject)
                    }
                }
            )

            HookEntry.log("Hooked $className#onInit")
        } catch (t: Throwable) {
            HookEntry.log(t)
        }
    }

    private fun replaceBySiblingText(instance: Any) {
        try {
            val shadeCarrierGroup =
                getObjectFieldSilently(instance, "mShadeCarrierGroup") as? View ?: run {
                    HookEntry.log("mShadeCarrierGroup not found or null")
                    return
                }

            val parent = shadeCarrierGroup.parent as? ViewGroup
            if (parent == null) {
                HookEntry.log("mShadeCarrierGroup parent is null")
                return
            }

            // 先隐藏原始运营商组，不破坏内部结构
            shadeCarrierGroup.visibility = View.GONE

            // 避免重复添加
            val existing = parent.findViewWithTag<View>(CUSTOM_TAG)
            if (existing != null) {
                existing.visibility = View.VISIBLE
                HookEntry.log("Custom QS text already exists")
                return
            }

            val textView = createCustomTextView(parent.context).apply {
                tag = CUSTOM_TAG
            }

            // 尽量插在原位置附近
            val index = parent.indexOfChild(shadeCarrierGroup)
            if (index >= 0) {
                parent.addView(textView, index)
            } else {
                parent.addView(textView)
            }

            parent.requestLayout()
            HookEntry.log("Inserted custom QS text as sibling")
        } catch (t: Throwable) {
            HookEntry.log(t)
        }
    }

    private fun createCustomTextView(context: Context): TextView {
        return TextView(context).apply {
            text = CUSTOM_TEXT
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun getObjectFieldSilently(instance: Any, fieldName: String): Any? {
        return try {
            XposedHelpers.getObjectField(instance, fieldName)
        } catch (_: Throwable) {
            null
        }
    }
}