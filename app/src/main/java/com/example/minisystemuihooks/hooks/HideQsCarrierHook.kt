package com.example.minisystemuihooks.hooks

import android.content.Context
import android.graphics.Color
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
                        replaceShadeCarrierGroupWithText(param.thisObject)
                    }
                }
            )

            HookEntry.log("Hooked $className#onInit")
        } catch (t: Throwable) {
            HookEntry.log(t)
        }
    }

    private fun replaceShadeCarrierGroupWithText(instance: Any) {
        try {
            val group = getObjectFieldSilently(instance, "mShadeCarrierGroup") as? ViewGroup
            if (group == null) {
                HookEntry.log("mShadeCarrierGroup not found or not ViewGroup")
                return
            }

            val context = group.context ?: run {
                HookEntry.log("mShadeCarrierGroup context is null")
                return
            }

            group.removeAllViews()

            val textView = createCustomTextView(context)
            group.addView(textView)

            group.visibility = View.VISIBLE
            group.requestLayout()

            HookEntry.log("Replaced mShadeCarrierGroup with custom text")
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
            isSingleLine = true
            ellipsize = android.text.TextUtils.TruncateAt.END
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