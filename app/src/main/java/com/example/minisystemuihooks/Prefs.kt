package com.example.minisystemuihooks

import android.content.Context

object Prefs {
    const val PREF_NAME = "module_config"

    const val KEY_HIDE_LOCKSCREEN_STATUSBAR = "hide_lockscreen_statusbar"
    const val KEY_HIDE_QS_CARRIER = "hide_qs_carrier"
    const val KEY_CLOCK_SIZE_ENABLED = "statusbar_clock_size_enabled"
    const val KEY_CLOCK_SIZE = "statusbar_clock_size"

    private fun dpContext(context: Context): Context {
        return context.createDeviceProtectedStorageContext()
    }

    private fun prefs(context: Context) =
        dpContext(context).getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getUiHideLockscreenStatusbar(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_HIDE_LOCKSCREEN_STATUSBAR, false)
    }

    fun getUiHideQsCarrier(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_HIDE_QS_CARRIER, false)
    }

    fun getUiClockSizeEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_CLOCK_SIZE_ENABLED, false)
    }

    fun getUiClockSize(context: Context): Int {
        return prefs(context).getInt(KEY_CLOCK_SIZE, 14)
    }

    fun setHideLockscreenStatusbar(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_HIDE_LOCKSCREEN_STATUSBAR, enabled).commit()
    }

    fun setHideQsCarrier(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_HIDE_QS_CARRIER, enabled).commit()
    }

    fun setClockSizeEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_CLOCK_SIZE_ENABLED, enabled).commit()
    }

    fun setClockSize(context: Context, size: Int) {
        prefs(context).edit().putInt(KEY_CLOCK_SIZE, size).commit()
    }
}