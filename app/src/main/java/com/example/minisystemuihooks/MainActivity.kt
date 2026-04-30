package com.example.minisystemuihooks

import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppPrefs.makePrefsWorldReadable(this)

        setContentView(R.layout.activity_main)

        val prefs = AppPrefs.getPrefs(this)

        val cbLockscreen = findViewById<CheckBox>(R.id.cbLockscreen)
        val cbQsCarrier = findViewById<CheckBox>(R.id.cbQsCarrier)

        cbLockscreen.isChecked =
            prefs.getBoolean(AppPrefs.KEY_HIDE_LOCKSCREEN_STATUSBAR, false)
        cbQsCarrier.isChecked =
            prefs.getBoolean(AppPrefs.KEY_HIDE_QS_CARRIER, false)

        cbLockscreen.setOnCheckedChangeListener { _, isChecked ->
            AppPrefs.setHideLockscreenStatusbar(this, isChecked)
        }

        cbQsCarrier.setOnCheckedChangeListener { _, isChecked ->
            AppPrefs.setHideQsCarrier(this, isChecked)
        }
    }
}