package com.example.minisystemuihooks

import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppConfig.ensureConfigExists(this)

        setContentView(R.layout.activity_main)

        val cbLockscreen = findViewById<CheckBox>(R.id.cbLockscreen)
        val cbQsCarrier = findViewById<CheckBox>(R.id.cbQsCarrier)

        cbLockscreen.isChecked = AppConfig.isHideLockscreenStatusbarEnabled(this)
        cbQsCarrier.isChecked = AppConfig.isHideQsCarrierEnabled(this)

        cbLockscreen.setOnCheckedChangeListener { _, isChecked ->
            AppConfig.setHideLockscreenStatusbar(this, isChecked)
        }

        cbQsCarrier.setOnCheckedChangeListener { _, isChecked ->
            AppConfig.setHideQsCarrier(this, isChecked)
        }
    }
}