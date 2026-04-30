package com.example.minisystemuihooks

import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cbLockscreen = findViewById<CheckBox>(R.id.cbLockscreen)
        val cbQsCarrier = findViewById<CheckBox>(R.id.cbQsCarrier)
        val cbFixedClockSize = findViewById<CheckBox>(R.id.cbFixedClockSize)

        cbLockscreen.isChecked = Prefs.getUiHideLockscreenStatusbar(this)
        cbQsCarrier.isChecked = Prefs.getUiHideQsCarrier(this)
        cbFixedClockSize.isChecked = Prefs.getUiFixedClockSizeEnabled(this)

        cbLockscreen.setOnCheckedChangeListener { _, isChecked ->
            Prefs.setUiHideLockscreenStatusbar(this, isChecked)
            Prefs.setHideLockscreenStatusbar(isChecked)
        }

        cbQsCarrier.setOnCheckedChangeListener { _, isChecked ->
            Prefs.setUiHideQsCarrier(this, isChecked)
            Prefs.setHideQsCarrier(isChecked)
        }

        cbFixedClockSize.setOnCheckedChangeListener { _, isChecked ->
            Prefs.setUiFixedClockSizeEnabled(this, isChecked)
            Prefs.setFixedClockSizeEnabled(isChecked)
        }
    }
}