package com.example.minisystemuihooks.hooks

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.minisystemuihooks.HookEntry
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LayoutInflated
import de.robv.android.xposed.callbacks.XC_LoadPackage

object HideQsCarrierHook {

    private const val SYSTEMUI = "com.android.systemui"
    private const val FORCE_ENABLE = true

    fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        try {
            resparam.res.hookLayout(
                SYSTEMUI,
                "layout",
                "quick_qs_status_icons",
                object : XC_LayoutInflated() {
                    override fun handleLayoutInflated(liparam: LayoutInflatedParam) {
                        HookEntry.log("quick_qs_status_icons inflated")
                        HookEntry.log("hide_qs_carrier forced=$FORCE_ENABLE")

                        if (!FORCE_ENABLE) return

                        try {
                            val carrierGroupId = liparam.res.getIdentifier(
                                "carrier_group",
                                "id",
                                SYSTEMUI
                            )

                            if (carrierGroupId == 0) {
                                HookEntry.log("carrier_group id not found")
                                return
                            }

                            val carrierGroup =
                                liparam.view.findViewById<LinearLayout>(carrierGroupId)

                            if (carrierGroup == null) {
                                HookEntry.log("carrier_group view not found")
                                return
                            }

                            carrierGroup.layoutParams?.height = 0
                            carrierGroup.layoutParams?.width = 0
                            carrierGroup.minimumWidth = 0
                            carrierGroup.visibility = View.INVISIBLE
                            carrierGroup.requestLayout()

                            HookEntry.log("QS carrier hidden by layout hook")
                        } catch (t: Throwable) {
                            HookEntry.log(t)
                        }
                    }
                }
            )
        } catch (_: Throwable) {
            HookEntry.log("quick_qs_status_icons layout not found, skip layout hook")
        }
    }

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        hookQuickStatusBarHeader(lpparam)
        hookShadeHeaderControllers(lpparam)
    }

    private fun hookQuickStatusBarHeader(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            val clazz = XposedHelpers.findClassIfExists(
                "com.android.systemui.qs.QuickStatusBarHeader",
                lpparam.classLoader
            )

            if (clazz == null) {
                HookEntry.log("QuickStatusBarHeader not found")
                return
            }

            XposedHelpers.findAndHookMethod(
                clazz,
                "onFinishInflate",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        HookEntry.log("QuickStatusBarHeader.onFinishInflate, hideQs=$FORCE_ENABLE")

                        if (!FORCE_ENABLE) return

                        val carriers = getObjectFieldSilently(param.thisObject, "mQSCarriers") as? View
                        if (carriers != null) {
                            carriers.visibility = View.INVISIBLE
                            HookEntry.log("mQSCarriers hidden in QuickStatusBarHeader")
                        } else {
                            HookEntry.log("mQSCarriers not found in QuickStatusBarHeader")
                        }
                    }
                }
            )

            HookEntry.log("Hooked QuickStatusBarHeader#onFinishInflate")
        } catch (t: Throwable) {
            HookEntry.log(t)
        }
    }

    private fun hookShadeHeaderControllers(lpparam: XC_LoadPackage.LoadPackageParam) {
        val classNames = listOf(
            "com.android.systemui.shade.LargeScreenShadeHeaderController",
            "com.android.systemui.shade.ShadeHeaderController"
        )

        classNames.forEach { name ->
            try {
                val clazz = XposedHelpers.findClassIfExists(name, lpparam.classLoader)
                if (clazz == null) {
                    HookEntry.log("$name not found")
                    return@forEach
                }

                XposedHelpers.findAndHookMethod(
                    clazz,
                    "onInit",
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            HookEntry.log("$name.onInit, hideQs=$FORCE_ENABLE")

                            if (!FORCE_ENABLE) return

                            hideOrRemoveFieldView(param.thisObject, "qsCarrierGroup")
                            hideOrRemoveFieldView(param.thisObject, "mShadeCarrierGroup")
                            hideOrRemoveFieldView(param.thisObject, "mQSCarriers")
                        }
                    }
                )

                HookEntry.log("Hooked $name#onInit")
            } catch (t: Throwable) {
                HookEntry.log(t)
            }
        }
    }

    private fun hideOrRemoveFieldView(instance: Any, fieldName: String) {
        try {
            val view = getObjectFieldSilently(instance, fieldName) as? View
            if (view == null) {
                HookEntry.log("Field $fieldName not found or null")
                return
            }

            val parent = view.parent as? ViewGroup
            if (parent != null) {
                parent.removeView(view)
                HookEntry.log("Removed carrier-related field: $fieldName")
            } else {
                view.visibility = View.INVISIBLE
                HookEntry.log("Set invisible for carrier-related field: $fieldName")
            }
        } catch (t: Throwable) {
            HookEntry.log("Failed on field $fieldName: ${t.message}")
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