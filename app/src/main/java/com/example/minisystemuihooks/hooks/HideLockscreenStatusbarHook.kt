package com.example.minisystemuihooks.hooks

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.minisystemuihooks.HookEntry
import com.example.minisystemuihooks.Prefs
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LayoutInflated

object HideLockscreenStatusbarHook {

    fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        try {
            resparam.res.hookLayout(
                "com.android.systemui",
                "layout",
                "keyguard_status_bar",
                object : XC_LayoutInflated() {
                    override fun handleLayoutInflated(liparam: LayoutInflatedParam) {
                        if (!Prefs.isHideLockscreenStatusbarEnabled()) return

                        try {
                            val statusIconAreaId = liparam.res.getIdentifier(
                                "status_icon_area",
                                "id",
                                "com.android.systemui"
                            )
                            if (statusIconAreaId != 0) {
                                val view = liparam.view.findViewById<LinearLayout>(statusIconAreaId)
                                view?.apply {
                                    layoutParams.height = 0
                                    visibility = View.INVISIBLE
                                    requestLayout()
                                }
                            }
                        } catch (t: Throwable) {
                            HookEntry.log(t)
                        }

                        try {
                            val carrierTextId = liparam.res.getIdentifier(
                                "keyguard_carrier_text",
                                "id",
                                "com.android.systemui"
                            )
                            if (carrierTextId != 0) {
                                val view = liparam.view.findViewById<TextView>(carrierTextId)
                                view?.apply {
                                    layoutParams.height = 0
                                    visibility = View.INVISIBLE
                                    requestLayout()
                                }
                            }
                        } catch (t: Throwable) {
                            HookEntry.log(t)
                        }

                        HookEntry.log("Lockscreen statusbar hidden")
                    }
                }
            )
        } catch (t: Throwable) {
            HookEntry.log(t)
        }
    }
}