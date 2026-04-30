package com.example.minisystemuihooks

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

object AppConfig {
    const val KEY_HIDE_LOCKSCREEN_STATUSBAR = "hide_lockscreen_statusbar"
    const val KEY_HIDE_QS_CARRIER = "hide_qs_carrier"

    private const val FILE_NAME = "config.properties"

    private fun getConfigFile(context: Context): File {
        return File(context.filesDir, FILE_NAME)
    }

    fun ensureConfigExists(context: Context) {
        val file = getConfigFile(context)
        if (!file.exists()) {
            val props = Properties().apply {
                setProperty(KEY_HIDE_LOCKSCREEN_STATUSBAR, "false")
                setProperty(KEY_HIDE_QS_CARRIER, "false")
            }
            FileOutputStream(file).use { props.store(it, "MiniSystemUIHooks config") }
        }
        makeConfigReadable(context)
    }

    fun isHideLockscreenStatusbarEnabled(context: Context): Boolean {
        return loadProps(context).getProperty(KEY_HIDE_LOCKSCREEN_STATUSBAR, "false").toBoolean()
    }

    fun isHideQsCarrierEnabled(context: Context): Boolean {
        return loadProps(context).getProperty(KEY_HIDE_QS_CARRIER, "false").toBoolean()
    }

    fun setHideLockscreenStatusbar(context: Context, enabled: Boolean) {
        val props = loadProps(context)
        props.setProperty(KEY_HIDE_LOCKSCREEN_STATUSBAR, enabled.toString())
        saveProps(context, props)
    }

    fun setHideQsCarrier(context: Context, enabled: Boolean) {
        val props = loadProps(context)
        props.setProperty(KEY_HIDE_QS_CARRIER, enabled.toString())
        saveProps(context, props)
    }

    private fun loadProps(context: Context): Properties {
        val props = Properties()
        val file = getConfigFile(context)

        if (!file.exists()) {
            ensureConfigExists(context)
        }

        try {
            FileInputStream(file).use { props.load(it) }
        } catch (t: Throwable) {
            Log.e("MiniSystemUIHooks", "loadProps failed", t)
        }

        return props
    }

    private fun saveProps(context: Context, props: Properties) {
        val file = getConfigFile(context)

        try {
            FileOutputStream(file).use { props.store(it, "MiniSystemUIHooks config") }
            makeConfigReadable(context)
        } catch (t: Throwable) {
            Log.e("MiniSystemUIHooks", "saveProps failed", t)
        }
    }

    fun makeConfigReadable(context: Context) {
        try {
            val filesDir = context.filesDir
            val configFile = getConfigFile(context)

            if (filesDir.exists()) {
                filesDir.setReadable(true, false)
                filesDir.setExecutable(true, false)
            }

            if (configFile.exists()) {
                configFile.setReadable(true, false)
            }

            Log.i("MiniSystemUIHooks", "Config readable: ${configFile.absolutePath}")
        } catch (t: Throwable) {
            Log.e("MiniSystemUIHooks", "makeConfigReadable failed", t)
        }
    }
}