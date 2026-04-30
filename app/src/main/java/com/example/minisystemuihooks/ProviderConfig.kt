package com.example.minisystemuihooks

import android.content.Context
import android.os.Bundle

object ProviderConfig {

    fun getBoolean(context: Context, key: String, defValue: Boolean = false): Boolean {
        return try {
            val result: Bundle? = context.contentResolver.call(
                ConfigProvider.URI,
                ConfigProvider.METHOD_GET_BOOL,
                key,
                null
            )
            result?.getBoolean(ConfigProvider.RESULT_VALUE, defValue) ?: defValue
        } catch (_: Throwable) {
            defValue
        }
    }

    fun getInt(context: Context, key: String, defValue: Int = 14): Int {
        return try {
            val result: Bundle? = context.contentResolver.call(
                ConfigProvider.URI,
                ConfigProvider.METHOD_GET_INT,
                key,
                null
            )
            result?.getInt(ConfigProvider.RESULT_VALUE, defValue) ?: defValue
        } catch (_: Throwable) {
            defValue
        }
    }
}