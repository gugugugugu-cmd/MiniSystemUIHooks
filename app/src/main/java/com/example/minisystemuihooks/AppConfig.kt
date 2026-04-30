package com.example.minisystemuihooks

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

object AppConfig {
    const val KEY_HIDE_LOCKSCREEN_STATUSBAR = "hide_lockscreen_statusbar"
    const val KEY_HIDE_QS_CARRIER = "hide_qs_carrier"

    private const val PACKAGE_NAME = "com.example.minisystemuihooks"
    private const val FILE_NAME = "config.properties"

    private fun getBaseDir(): File {
        return File(
            Environment.getExternalStorageDirectory(),
            "Android/media/$PACKAGE_NAME"
        )
    }

    fun getConfigFile(): File {
        return File(getBaseDir(), FILE_NAME)
    }

    fun ensureConfigExists(context: Context) {
        val dir = getBaseDir()
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val file = getConfigFile()
        if (!file.exists()) {
            val props = Properties().apply {
                setProperty(KEY_HIDE_LOCKSCREEN_STATUSBAR, "false")
                setProperty(KEY_HIDE_QS_CARRIER, "false")
            }
            FileOutputStream(file).use { props.store(it, "MiniSystemUIHooks config") }
        }

        Log.i("MiniSystemUIHooks", "Config path: ${file.absolutePath}")
    }

    fun isHideLockscreenStatusbarEnabled(context: Context): Boolean {
        return loadProps().getProperty(KEY_HIDE_LOCKSCREEN_STATUSBAR, "false").toBoolean()
    }

    fun isHideQsCarrierEnabled(context: Context): Boolean {
        return loadProps().getProperty(KEY_HIDE_QS_CARRIER, "false").toBoolean()
    }

    fun setHideLockscreenStatusbar(context: Context, enabled: Boolean) {
        val props = loadProps()
        props.setProperty(KEY_HIDE_LOCKSCREEN_STATUSBAR, enabled.toString())
        saveProps(props)
    }

    fun setHideQsCarrier(context: Context, enabled: Boolean) {
        val props = loadProps()
        props.setProperty(KEY_HIDE_QS_CARRIER, enabled.toString())
        saveProps(props)
    }

    private fun loadProps(): Properties {
        val props = Properties()
        val file = getConfigFile()

        try {
            if (!file.exists()) {
                val dir = getBaseDir()
                if (!dir.exists()) dir.mkdirs()
                FileOutputStream(file).use {
                    Properties().apply {
                        setProperty(KEY_HIDE_LOCKSCREEN_STATUSBAR, "false")
                        setProperty(KEY_HIDE_QS_CARRIER, "false")
                        store(it, "MiniSystemUIHooks config")
                    }
                }
            }

            FileInputStream(file).use { props.load(it) }
        } catch (t: Throwable) {
            Log.e("MiniSystemUIHooks", "loadProps failed", t)
        }

        return props
    }

    private fun saveProps(props: Properties) {
        val dir = getBaseDir()
        if (!dir.exists()) dir.mkdirs()

        val file = getConfigFile()

        try {
            FileOutputStream(file).use { props.store(it, "MiniSystemUIHooks config") }
            Log.i("MiniSystemUIHooks", "Config saved: ${file.absolutePath}")
        } catch (t: Throwable) {
            Log.e("MiniSystemUIHooks", "saveProps failed", t)
        }
    }
}