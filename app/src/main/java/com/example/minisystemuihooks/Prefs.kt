package com.example.minisystemuihooks

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

object Prefs {
    private const val PACKAGE_NAME = "com.example.minisystemuihooks"

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
        } catch (t: Throwable) {
            HookEntry.log(t)
        }
        return props
    }

    private fun writeProps(props: Properties) {
        try {
            val file = getConfigFile()
            FileOutputStream(file).use { props.store(it, "MiniSystemUIHooks config") }
            file.setReadable(true, false)
            HookEntry.log("Config written: ${file.absolutePath}")
        } catch (t: Throwable) {
            HookEntry.log(t)
        }
    }

    fun isClockSizeEnabled(): Boolean {
        return try {
            val result = readProps()
                .getProperty(KEY_CLOCK_SIZE_ENABLED, "false")
                .toBoolean()
            HookEntry.log("File pref statusbar_clock_size_enabled=$result")
            result
        } catch (t: Throwable) {
            HookEntry.log(t)
            false
        }
    }

    fun getClockSize(): Int {
        return try {
            val result = readProps()
                .getProperty(KEY_CLOCK_SIZE, "14")
                .toInt()
            HookEntry.log("File pref statusbar_clock_size=$result")
            result
        } catch (t: Throwable) {
            HookEntry.log(t)
            14
        }
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

    fun getUiClockSizeEnabled(context: Context): Boolean {
        return context.getSharedPreferences("ui_settings", Context.MODE_PRIVATE)
            .getBoolean(KEY_CLOCK_SIZE_ENABLED, false)
    }

    fun getUiClockSize(context: Context): Int {
        return context.getSharedPreferences("ui_settings", Context.MODE_PRIVATE)
            .getInt(KEY_CLOCK_SIZE, 14)
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