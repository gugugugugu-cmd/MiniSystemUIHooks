package com.example.minisystemuihooks.hooks

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.minisystemuihooks.HookEntry
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LayoutInflated

object HideLockscreenStatusbarHook {

    private const val SYSTEMUI = "com.android.systemui"
    private const val FORCE_ENABLE = true

    fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        try {
            resparam.res.hookLayout(
                SYSTEMUI,
                "layout",
                "keyguard_status_bar",
                object : XC_LayoutInflated() {
                    override fun handleLayoutInflated(liparam: LayoutInflatedParam) {
                        HookEntry.log("keyguard_status_bar inflated")
                        HookEntry.log("hide_lockscreen_statusbar forced=$FORCE_ENABLE")

                        if (!FORCE_ENABLE) return

                        hideStatusIconArea(liparam)
                        hideCarrierText(liparam)

                        HookEntry.log("Lockscreen statusbar hide logic applied")
                    }
                }
            )
        } catch (_: Throwable) {
            HookEntry.log("keyguard_status_bar layout not found, skip")
        }
    }

    private fun hideStatusIconArea(liparam: XC_LayoutInflated.LayoutInflatedParam) {
        try {
            val id = liparam.res.getIdentifier(
                "status_icon_area",
                "id",
                SYSTEMUI
            )

            if (id == 0) {
                HookEntry.log("status_icon_area id not found")
                return
            }

            val view = liparam.view.findViewById<LinearLayout>(id)
            if (view == null) {
                HookEntry.log("status_icon_area view not found")
                return
            }

            view.layoutParams?.height = 0
            view.visibility = View.INVISIBLE
            view.requestLayout()

            HookEntry.log("status_icon_area hidden")
        } catch (t: Throwable) {
            HookEntry.log(t)
        }
    }

    private fun hideCarrierText(liparam: XC_LayoutInflated.LayoutInflatedParam) {
        try {
            val id = liparam.res.getIdentifier(
                "keyguard_carrier_text",
                "id",
                SYSTEMUI
            )

            if (id == 0) {
                HookEntry.log("keyguard_carrier_text id not found")
                return
            }

            val view = liparam.view.findViewById<TextView>(id)
            if (view == null) {
                HookEntry.log("keyguard_carrier_text view not found")
                return
            }

            view.layoutParams?.height = 0
            view.visibility = View.INVISIBLE
            view.requestLayout()

            HookEntry.log("keyguard_carrier_text hidden")
        } catch (t: Throwable) {
            HookEntry.log(t)
        }
    }
}