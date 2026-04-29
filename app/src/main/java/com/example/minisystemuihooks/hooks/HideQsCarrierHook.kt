package com.example.minisystemuihooks.hooks

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.minisystemuihooks.HookEntry
import com.example.minisystemuihooks.Prefs
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LayoutInflated
import de.robv.android.xposed.callbacks.XC_LoadPackage

object HideQsCarrierHook {

    fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        try {
            resparam.res.hookLayout(
                "com.android.systemui",
                "layout",
                "quick_qs_status_icons",
                object : XC_LayoutInflated() {
                    override fun handleLayoutInflated(liparam: LayoutInflatedParam) {
                        if (!Prefs.isHideQsCarrierEnabled()) return

                        try {
                            val carrierGroupId = liparam.res.getIdentifier(
                                "carrier_group",
                                "id",
                                "com.android.systemui"
                            )

                            if (carrierGroupId != 0) {
                                val carrierGroup =
                                    liparam.view.findViewById<LinearLayout>(carrierGroupId)

                                carrierGroup?.apply {
                                    layoutParams.height = 0
                                    layoutParams.width = 0
                                    minimumWidth = 0
                                    visibility = View.INVISIBLE
                                    requestLayout()
                                }
                            }

                            HookEntry.log("QS carrier hidden by layout hook")
                        } catch (t: Throwable) {
                            HookEntry.log(t)
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            HookEntry.log(t)
        }
    }

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        hookQuickStatusBarHeader(lpparam)
        hookShadeHeaderController(lpparam)
    }

    private fun hookQuickStatusBarHeader(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            val clazz = XposedHelpers.findClassIfExists(
                "com.android.systemui.qs.QuickStatusBarHeader",
                lpparam.classLoader
            ) ?: return

            XposedHelpers.findAndHookMethod(
                clazz,
                "onFinishInflate",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (!Prefs.isHideQsCarrierEnabled()) return

                        try {
                            val carriers = XposedHelpers.getObjectField(
                                param.thisObject,
                                "mQSCarriers"
                            ) as? View
                            carriers?.visibility = View.INVISIBLE
                            HookEntry.log("QS carrier hidden in QuickStatusBarHeader")
                        } catch (t: Throwable) {
                            HookEntry.log(t)
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            HookEntry.log(t)
        }
    }

    private fun hookShadeHeaderController(lpparam: XC_LoadPackage.LoadPackageParam) {
        val classNames = listOf(
            "com.android.systemui.shade.LargeScreenShadeHeaderController",
            "com.android.systemui.shade.ShadeHeaderController"
        )

        classNames.forEach { name ->
            try {
                val clazz = XposedHelpers.findClassIfExists(name, lpparam.classLoader) ?: return@forEach

                XposedHelpers.findAndHookMethod(
                    clazz,
                    "onInit",
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            if (!Prefs.isHideQsCarrierEnabled()) return

                            removeCarrierGroup(param.thisObject, "qsCarrierGroup")
                            removeCarrierGroup(param.thisObject, "mShadeCarrierGroup")
                        }
                    }
                )

                HookEntry.log("Hooked $name#onInit")
            } catch (t: Throwable) {
                HookEntry.log(t)
            }
        }
    }

    private fun removeCarrierGroup(instance: Any, fieldName: String) {
        try {
            val group = XposedHelpers.getObjectField(instance, fieldName) as? LinearLayout ?: return
            val parent = group.parent as? ViewGroup
            parent?.removeView(group)
            HookEntry.log("Removed carrier group field: $fieldName")
        } catch (_: Throwable) {
        }
    }
}