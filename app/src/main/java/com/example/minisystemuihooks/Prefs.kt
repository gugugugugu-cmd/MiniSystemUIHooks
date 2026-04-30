package com.example.minisystemuihooks

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

object Prefs {
    private const val PACKAGE_NAME = "com.example.minisystemuihooks"

    const val KEY_HIDE_LOCKSCREEN_STATUSBAR = "hide_lockscreen_statusbar"
    const val KEY_HIDE_QS_CARRIER = "hide_qs_carrier"
    const val KEY_CLOCK_SIZE_ENABLED = "statusbar_clock_size_enabled"
    const val KEY_CLOCK_SIZE = "statusbar_clock_size"

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
        } catch (_: Throwable) {
        }
        return props
    }

    private fun writeProps(props: Properties) {
        try {
            val file = getConfigFile()
            FileOutputStream(file).use { props.store(it, "MiniSystemUIHooks config") }
            file.setReadable(true, false)
        } catch (_: Throwable) {
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

    fun isClockSizeEnabled(): Boolean {
        return try {
            readProps().getProperty(KEY_CLOCK_SIZE_ENABLED, "false").toBoolean()
        } catch (_: Throwable) {
            false
        }
    }

    fun getClockSize(): Int {
        return try {
            readProps().getProperty(KEY_CLOCK_SIZE, "14").toInt()
        } catch (_: Throwable) {
            14
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

    fun setClockSizeEnabled(enabled: Boolean) {
        val props = readProps()
        props.setProperty(KEY_CLOCK_SIZE_ENABLED, enabled.toString())
        writeProps(props)
    }

    fun setClockSize(size: Int) {
        val props = readProps()
        props.setProperty(KEY_CLOCK_SIZE, size.toString())
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

    fun getUiClockSizeEnabled(context: Context): Boolean {
        return context.getSharedPreferences("ui_settings", Context.MODE_PRIVATE)
            .getBoolean(KEY_CLOCK_SIZE_ENABLED, false)
    }

    fun getUiClockSize(context: Context): Int {
        return context.getSharedPreferences("ui_settings", Context.MODE_PRIVATE)
            .getInt(KEY_CLOCK_SIZE, 14)
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

    fun setUiClockSizeEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences("ui_settings", Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_CLOCK_SIZE_ENABLED, enabled)
            .apply()
    }

    fun setUiClockSize(context: Context, size: Int) {
        context.getSharedPreferences("ui_settings", Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_CLOCK_SIZE, size)
            .apply()
    }
}