package com.example.minisystemuihooks

import de.robv.android.xposed.XSharedPreferences

object HookPrefs {
    private const val PACKAGE_NAME = "com.example.minisystemuihooks"
    private const val PREF_NAME = "settings"

    private const val KEY_HIDE_LOCKSCREEN_STATUSBAR = "hide_lockscreen_statusbar"
    private const val KEY_HIDE_QS_CARRIER = "hide_qs_carrier"

    fun isHideLockscreenStatusbarEnabled(): Boolean {
        return try {
            val prefs = XSharedPreferences(PACKAGE_NAME, PREF_NAME)
            prefs.reload()
            val result = prefs.getBoolean(KEY_HIDE_LOCKSCREEN_STATUSBAR, false)
            HookEntry.log("Pref hide_lockscreen_statusbar=$result")
            result
        } catch (t: Throwable) {
            HookEntry.log(t)
            false
        }
    }

    fun isHideQsCarrierEnabled(): Boolean {
        return try {
            val prefs = XSharedPreferences(PACKAGE_NAME, PREF_NAME)
            prefs.reload()
            val result = prefs.getBoolean(KEY_HIDE_QS_CARRIER, false)
            HookEntry.log("Pref hide_qs_carrier=$result")
            result
        } catch (t: Throwable) {
            HookEntry.log(t)
            false
        }
    }
}