package com.example.minisystemuihooks.hooks

import android.view.View
import android.view.ViewGroup
import com.example.minisystemuihooks.HookEntry
import com.example.minisystemuihooks.HookPrefs
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
            if (clazz == null) {
                HookEntry.log("$className not found")
                return
            }

            XposedHelpers.findAndHookMethod(
                clazz,
                "onInit",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val enabled = HookPrefs.isHideQsCarrierEnabled()
                        HookEntry.log("$className.onInit, hideQs=$enabled")
                        if (!enabled) return

                        removeShadeCarrierGroup(param.thisObject)
                    }
                }
            )

            HookEntry.log("Hooked $className#onInit")
        } catch (t: Throwable) {
            HookEntry.log(t)
        }
    }

    private fun removeShadeCarrierGroup(instance: Any) {
        try {
            val view = getObjectFieldSilently(instance, "mShadeCarrierGroup") as? View
            if (view == null) {
                HookEntry.log("mShadeCarrierGroup not found or null")
                return
            }

            val parent = view.parent as? ViewGroup
            if (parent != null) {
                parent.removeView(view)
                HookEntry.log("Removed mShadeCarrierGroup")
            } else {
                view.visibility = View.INVISIBLE
                HookEntry.log("Set invisible for mShadeCarrierGroup")
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