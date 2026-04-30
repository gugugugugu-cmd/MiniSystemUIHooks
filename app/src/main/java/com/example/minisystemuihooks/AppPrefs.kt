package com.example.minisystemuihooks

import android.content.Context
import android.util.Log
import java.io.File

object AppPrefs {
    private const val PREF_NAME = "settings"

    const val KEY_HIDE_LOCKSCREEN_STATUSBAR = "hide_lockscreen_statusbar"
    const val KEY_HIDE_QS_CARRIER = "hide_qs_carrier"

    fun getPrefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun setHideLockscreenStatusbar(context: Context, enabled: Boolean) {
        getPrefs(context).edit()
            .putBoolean(KEY_HIDE_LOCKSCREEN_STATUSBAR, enabled)
            .commit()

        makePrefsWorldReadable(context)
    }

    fun setHideQsCarrier(context: Context, enabled: Boolean) {
        getPrefs(context).edit()
            .putBoolean(KEY_HIDE_QS_CARRIER, enabled)
            .commit()

        makePrefsWorldReadable(context)
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

            Log.i("MiniSystemUIHooks", "Prefs readable: ${prefsFile.absolutePath}")
        } catch (t: Throwable) {
            Log.e("MiniSystemUIHooks", "makePrefsWorldReadable failed", t)
        }
    }
}