package com.example.minisystemuihooks.hooks

import android.view.View
import android.view.ViewGroup
import com.example.minisystemuihooks.Prefs
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object HideQsCarrierHook {

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        hookShadeHeaderController(lpparam)
    }

    private fun hookShadeHeaderController(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.systemui.shade.ShadeHeaderController"

        try {
            val clazz = XposedHelpers.findClassIfExists(className, lpparam.classLoader)
                ?: return

            XposedHelpers.findAndHookMethod(
                clazz,
                "onInit",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val enabled = Prefs.isHideQsCarrierEnabled()
                        if (!enabled) return

                        removeShadeCarrierGroup(param.thisObject)
                    }
                }
            )
        } catch (t: Throwable) {
            HookEntry.log(t)
        }
    }

    private fun removeShadeCarrierGroup(instance: Any) {
        try {
            val view = getObjectFieldSilently(instance, "mShadeCarrierGroup") as? View ?: return

            val parent = view.parent as? ViewGroup
            if (parent != null) {
                parent.removeView(view)
            } else {
                view.visibility = View.INVISIBLE
            }
        } catch (t: Throwable) {
            HookEntry.log(t)
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