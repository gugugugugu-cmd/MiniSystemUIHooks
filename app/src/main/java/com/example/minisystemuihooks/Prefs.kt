package com.example.minisystemuihooks

import android.content.Context
import de.robv.android.xposed.XSharedPreferences
import java.io.File

object Prefs {
    private const val PACKAGE_NAME = "com.example.minisystemuihooks"
    private const val PREF_NAME = "settings"

    const val KEY_HIDE_LOCKSCREEN_STATUSBAR = "hide_lockscreen_statusbar"
    const val KEY_HIDE_QS_CARRIER = "hide_qs_carrier"

    fun getAppPrefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun setHideLockscreenStatusbar(context: Context, enabled: Boolean) {
        getAppPrefs(context).edit()
            .putBoolean(KEY_HIDE_LOCKSCREEN_STATUSBAR, enabled)
            .commit()

        makePrefsWorldReadable(context)
    }

    fun setHideQsCarrier(context: Context, enabled: Boolean) {
        getAppPrefs(context).edit()
            .putBoolean(KEY_HIDE_QS_CARRIER, enabled)
            .commit()

        makePrefsWorldReadable(context)
    }

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

    fun makePrefsWorldReadable(context: Context) {
        try {
            val dataDir = context.applicationInfo.dataDir
            val prefsDir = File("$dataDir/shared_prefs")
            val prefsFile = File(prefsDir, "$PREF_NAME.xml")

            if (prefsDir.exists()) {
                prefsDir.setReadable(true, false)
                prefsDir.setExecutable(true, false)
            }

            if (prefsFile.exists()) {
                prefsFile.setReadable(true, false)
            }

            HookEntry.log("Prefs file made readable: ${prefsFile.absolutePath}")
        } catch (t: Throwable) {
            HookEntry.log(t)
        }
    }
}