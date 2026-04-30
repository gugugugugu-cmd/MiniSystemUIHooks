package com.example.minisystemuihooks

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

object Prefs {
    private const val TAG = "MiniSystemUIHooksPrefs"
    private const val PACKAGE_NAME = "com.example.minisystemuihooks"

    const val KEY_HIDE_LOCKSCREEN_STATUSBAR = "hide_lockscreen_statusbar"
    const val KEY_HIDE_QS_CARRIER = "hide_qs_carrier"
    const val KEY_ENABLE_FIXED_CLOCK_SIZE = "enable_fixed_clock_size"

    private fun getConfigDir(): File {
        val base = Environment.getExternalStorageDirectory()
        return File(base, "Android/media/$PACKAGE_NAME").apply {
            if (!exists()) mkdirs()
        }
    }

    private fun getConfigFile(): File {
        return File(getConfigDir(), "config.properties")
    }

    private fun readProps(): Properties {
        val props = Properties()
        try {
            val file = getConfigFile()
            if (file.exists()) {
                FileInputStream(file).use { props.load(it) }
            }
        } catch (t: Throwable) {
            Log.e(TAG, "readProps failed", t)
        }
        return props
    }

    private fun writeProps(props: Properties) {
        try {
            val file = getConfigFile()
            FileOutputStream(file).use { props.store(it, "MiniSystemUIHooks config") }
            file.setReadable(true, false)
            Log.d(TAG, "Config written: ${file.absolutePath}")
        } catch (t: Throwable) {
            Log.e(TAG, "writeProps failed", t)
        }
    }

    fun isHideLockscreenStatusbarEnabled(): Boolean {
        return try {
            readProps().getProperty(KEY_HIDE_LOCKSCREEN_STATUSBAR, "false").toBoolean()
        } catch (_: Throwable) {
            false
        }
    }

    fun isHideQsCarrierEnabled(): Boolean {
        return try {
            readProps().getProperty(KEY_HIDE_QS_CARRIER, "false").toBoolean()
        } catch (_: Throwable) {
            false
        }
    }

    fun isFixedClockSizeEnabled(): Boolean {
        return try {
            readProps().getProperty(KEY_ENABLE_FIXED_CLOCK_SIZE, "false").toBoolean()
        } catch (_: Throwable) {
            false
        }
    }

    fun setHideLockscreenStatusbar(enabled: Boolean) {
        val props = readProps()
        props.setProperty(KEY_HIDE_LOCKSCREEN_STATUSBAR, enabled.toString())
        writeProps(props)
    }

    fun setHideQsCarrier(enabled: Boolean) {
        val props = readProps()
        props.setProperty(KEY_HIDE_QS_CARRIER, enabled.toString())
        writeProps(props)
    }

    fun setFixedClockSizeEnabled(enabled: Boolean) {
        val props = readProps()
        props.setProperty(KEY_ENABLE_FIXED_CLOCK_SIZE, enabled.toString())
        writeProps(props)
    }

    fun getUiHideLockscreenStatusbar(context: Context): Boolean {
        return context.getSharedPreferences("ui_settings", Context.MODE_PRIVATE)
            .getBoolean(KEY_HIDE_LOCKSCREEN_STATUSBAR, false)
    }

    fun getUiHideQsCarrier(context: Context): Boolean {
        return context.getSharedPreferences("ui_settings", Context.MODE_PRIVATE)
            .getBoolean(KEY_HIDE_QS_CARRIER, false)
    }

    fun getUiFixedClockSizeEnabled(context: Context): Boolean {
        return context.getSharedPreferences("ui_settings", Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLE_FIXED_CLOCK_SIZE, false)
    }

    fun setUiHideLockscreenStatusbar(context: Context, enabled: Boolean) {
        context.getSharedPreferences("ui_settings", Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_HIDE_LOCKSCREEN_STATUSBAR, enabled)
            .apply()
    }

    fun setUiHideQsCarrier(context: Context, enabled: Boolean) {
        context.getSharedPreferences("ui_settings", Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_HIDE_QS_CARRIER, enabled)
            .apply()
    }

    fun setUiFixedClockSizeEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences("ui_settings", Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ENABLE_FIXED_CLOCK_SIZE, enabled)
            .apply()
    }
}