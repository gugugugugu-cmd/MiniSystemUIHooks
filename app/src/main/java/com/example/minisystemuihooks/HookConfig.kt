package com.example.minisystemuihooks

import java.io.File
import java.io.FileInputStream
import java.util.Properties

object HookConfig {
    private const val PACKAGE_NAME = "com.example.minisystemuihooks"
    private const val FILE_NAME = "config.properties"

    private const val KEY_HIDE_LOCKSCREEN_STATUSBAR = "hide_lockscreen_statusbar"
    private const val KEY_HIDE_QS_CARRIER = "hide_qs_carrier"

    private fun getConfigFile(): File {
        return File(
            "/storage/emulated/0/Android/media/$PACKAGE_NAME/$FILE_NAME"
        )
    }

    private fun loadProps(): Properties {
        val props = Properties()
        val file = getConfigFile()

        try {
            if (file.exists()) {
                FileInputStream(file).use { props.load(it) }
                HookEntry.log("Loaded config: ${file.absolutePath}")
            } else {
                HookEntry.log("Config file not found: ${file.absolutePath}")
            }
        } catch (t: Throwable) {
            HookEntry.log(t)
        }

        return props
    }

    fun isHideLockscreenStatusbarEnabled(): Boolean {
        return try {
            val result = loadProps()
                .getProperty(KEY_HIDE_LOCKSCREEN_STATUSBAR, "false")
                .toBoolean()
            HookEntry.log("Config hide_lockscreen_statusbar=$result")
            result
        } catch (t: Throwable) {
            HookEntry.log(t)
            false
        }
    }

    fun isHideQsCarrierEnabled(): Boolean {
        return try {
            val result = loadProps()
                .getProperty(KEY_HIDE_QS_CARRIER, "false")
                .toBoolean()
            HookEntry.log("Config hide_qs_carrier=$result")
            result
        } catch (t: Throwable) {
            HookEntry.log(t)
            false
        }
    }
}