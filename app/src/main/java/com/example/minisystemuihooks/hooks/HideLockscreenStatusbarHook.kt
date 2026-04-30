package com.example.minisystemuihooks.hooks

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.minisystemuihooks.HookEntry
import com.example.minisystemuihooks.Prefs
import com.example.minisystemuihooks.ProviderConfig
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LayoutInflated

object HideLockscreenStatusbarHook {

    private const val SYSTEMUI = "com.android.systemui"

    fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        try {
            resparam.res.hookLayout(
                SYSTEMUI,
                "layout",
                "keyguard_status_bar",
                object : XC_LayoutInflated() {
                    override fun handleLayoutInflated(liparam: LayoutInflatedParam) {
                        val enabled = ProviderConfig.getBoolean(
                            liparam.view.context,
                            Prefs.KEY_HIDE_LOCKSCREEN_STATUSBAR,
                            false
                        )
                        if (!enabled) return

                        hideStatusIconArea(liparam)
                        hideCarrierText(liparam)
                    }
                }
            )
        } catch (_: Throwable) {
            // ignore
        }
    }

    private fun hideStatusIconArea(liparam: XC_LayoutInflated.LayoutInflatedParam) {
        try {
            val id = liparam.res.getIdentifier("status_icon_area", "id", SYSTEMUI)
            if (id == 0) return

            val view = liparam.view.findViewById<LinearLayout>(id) ?: return
            view.layoutParams?.height = 0
            view.visibility = View.INVISIBLE
            view.requestLayout()
        } catch (t: Throwable) {
            HookEntry.log(t)
        }
    }

    private fun hideCarrierText(liparam: XC_LayoutInflated.LayoutInflatedParam) {
        try {
            val id = liparam.res.getIdentifier("keyguard_carrier_text", "id", SYSTEMUI)
            if (id == 0) return

            val view = liparam.view.findViewById<TextView>(id) ?: return
            view.layoutParams?.height = 0
            view.visibility = View.INVISIBLE
            view.requestLayout()
        } catch (t: Throwable) {
            HookEntry.log(t)
        }
    }
}