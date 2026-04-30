package com.example.minisystemuihooks

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppConfig.ensureConfigExists(this)

        setContentView(R.layout.activity_main)

        val cbLockscreen = findViewById<CheckBox>(R.id.cbLockscreen)
        val cbQsCarrier = findViewById<CheckBox>(R.id.cbQsCarrier)
        val btnRestartSystemUI = findViewById<Button>(R.id.btnRestartSystemUI)

        cbLockscreen.isChecked = AppConfig.isHideLockscreenStatusbarEnabled(this)
        cbQsCarrier.isChecked = AppConfig.isHideQsCarrierEnabled(this)

        cbLockscreen.setOnCheckedChangeListener { _, isChecked ->
            AppConfig.setHideLockscreenStatusbar(this, isChecked)
            toast("设置已保存")
        }

        cbQsCarrier.setOnCheckedChangeListener { _, isChecked ->
            AppConfig.setHideQsCarrier(this, isChecked)
            toast("设置已保存")
        }

        btnRestartSystemUI.setOnClickListener {
            val success = restartSystemUIWithRoot()
            if (success) {
                toast("已尝试重启 SystemUI")
            } else {
                toast("重启失败，请手动重启 SystemUI 或重启手机")
            }
        }
    }

    private fun restartSystemUIWithRoot(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "pkill -f com.android.systemui"))
            val result = process.waitFor()
            result == 0
        } catch (_: Throwable) {
            false
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}