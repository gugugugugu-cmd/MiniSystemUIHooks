package com.example.minisystemuihooks

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle

class ConfigProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.minisystemuihooks.config"
        val URI: Uri = Uri.parse("content://$AUTHORITY")

        const val METHOD_GET_BOOL = "get_bool"
        const val METHOD_GET_INT = "get_int"

        const val RESULT_VALUE = "value"
    }

    override fun onCreate(): Boolean = true

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle {
        val context = context ?: return Bundle()

        val result = Bundle()

        when (method) {
            METHOD_GET_BOOL -> {
                val value = when (arg) {
                    Prefs.KEY_HIDE_LOCKSCREEN_STATUSBAR ->
                        Prefs.getUiHideLockscreenStatusbar(context)

                    Prefs.KEY_HIDE_QS_CARRIER ->
                        Prefs.getUiHideQsCarrier(context)

                    Prefs.KEY_CLOCK_SIZE_ENABLED ->
                        Prefs.getUiClockSizeEnabled(context)

                    else -> false
                }
                result.putBoolean(RESULT_VALUE, value)
            }

            METHOD_GET_INT -> {
                val value = when (arg) {
                    Prefs.KEY_CLOCK_SIZE ->
                        Prefs.getUiClockSize(context)

                    else -> 14
                }
                result.putInt(RESULT_VALUE, value)
            }
        }

        return result
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0
}