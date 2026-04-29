package com.example.minisystemuihooks

import android.content.Context
import de.robv.android.xposed.XSharedPreferences

object Prefs {
    private const val PACKAGE_NAME = "com.example.minisystemuihooks"
    private const val PREF_NAME = "settings"

    const val KEY_HIDE_LOCKSCREEN_STATUSBAR = "hide_lockscreen_statusbar"
    const val KEY_HIDE_QS_CARRIER = "hide_qs_carrier"

    fun getAppPrefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun isHideLockscreenStatusbarEnabled(): Boolean {
        return try {
            val prefs = XSharedPreferences(PACKAGE_NAME, PREF_NAME)
            prefs.reload()
            prefs.getBoolean(KEY_HIDE_LOCKSCREEN_STATUSBAR, false)
        } catch (_: Throwable) {
            false
        }
    }

    fun isHideQsCarrierEnabled(): Boolean {
        return try {
            val prefs = XSharedPreferences(PACKAGE_NAME, PREF_NAME)
            prefs.reload()
            prefs.getBoolean(KEY_HIDE_QS_CARRIER, false)
        } catch (_: Throwable) {
            false
        }
    }
}