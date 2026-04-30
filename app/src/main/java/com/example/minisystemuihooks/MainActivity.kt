package com.example.minisystemuihooks

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var currentClockSize = 14

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cbLockscreen = findViewById<CheckBox>(R.id.cbLockscreen)
        val cbQsCarrier = findViewById<CheckBox>(R.id.cbQsCarrier)
        val cbEnableClockSize = findViewById<CheckBox>(R.id.cbEnableClockSize)

        val tvClockSizeTitle = findViewById<TextView>(R.id.tvClockSizeTitle)
        val tvClockSizeValue = findViewById<TextView>(R.id.tvClockSizeValue)
        val layoutClockButtons = findViewById<LinearLayout>(R.id.layoutClockButtons)
        val btnMinus = findViewById<Button>(R.id.btnMinus)
        val btnPlus = findViewById<Button>(R.id.btnPlus)

        // 读取 UI 状态
        cbLockscreen.isChecked = Prefs.getUiHideLockscreenStatusbar(this)
        cbQsCarrier.isChecked = Prefs.getUiHideQsCarrier(this)
        cbEnableClockSize.isChecked = Prefs.getUiClockSizeEnabled(this)

        currentClockSize = Prefs.getUiClockSize(this).coerceIn(8, 40)

        fun updateClockSizeText() {
            tvClockSizeValue.text = "${currentClockSize}sp"
        }

        fun updateClockSizeControlsVisibility() {
            val visible = if (cbEnableClockSize.isChecked) View.VISIBLE else View.GONE
            tvClockSizeTitle.visibility = visible
            tvClockSizeValue.visibility = visible
            layoutClockButtons.visibility = visible
        }

        updateClockSizeText()
        updateClockSizeControlsVisibility()

        // 隐藏锁屏状态栏
        cbLockscreen.setOnCheckedChangeListener { _, isChecked ->
            Prefs.setUiHideLockscreenStatusbar(this, isChecked)
            Prefs.setHideLockscreenStatusbar(isChecked)
        }

        // 隐藏 QS 运营商组
        cbQsCarrier.setOnCheckedChangeListener { _, isChecked ->
            Prefs.setUiHideQsCarrier(this, isChecked)
            Prefs.setHideQsCarrier(isChecked)
        }

        // 启用自定义状态栏时钟大小
        cbEnableClockSize.setOnCheckedChangeListener { _, isChecked ->
            Prefs.setUiClockSizeEnabled(this, isChecked)
            Prefs.setClockSizeEnabled(isChecked)
            updateClockSizeControlsVisibility()
        }

        // 减小字号
        btnMinus.setOnClickListener {
            if (currentClockSize > 8) {
                currentClockSize--
                updateClockSizeText()
                Prefs.setUiClockSize(this, currentClockSize)
                Prefs.setClockSize(currentClockSize)
            }
        }

        // 增大字号
        btnPlus.setOnClickListener {
            if (currentClockSize < 40) {
                currentClockSize++
                updateClockSizeText()
                Prefs.setUiClockSize(this, currentClockSize)
                Prefs.setClockSize(currentClockSize)
            }
        }
    }
}