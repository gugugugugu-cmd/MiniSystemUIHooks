package com.example.minisystemuihooks

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var currentSize = 14

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cbEnable = findViewById<CheckBox>(R.id.cbEnableClockSize)
        val tvSize = findViewById<TextView>(R.id.tvClockSizeValue)
        val btnMinus = findViewById<Button>(R.id.btnMinus)
        val btnPlus = findViewById<Button>(R.id.btnPlus)

        currentSize = Prefs.getUiClockSize(this).coerceIn(8, 40)
        cbEnable.isChecked = Prefs.getUiClockSizeEnabled(this)

        fun updateSizeText() {
            tvSize.text = "${currentSize}sp"
        }

        updateSizeText()

        cbEnable.setOnCheckedChangeListener { _, isChecked ->
            Prefs.setUiClockSizeEnabled(this, isChecked)
            Prefs.setClockSizeEnabled(isChecked)
        }

        btnMinus.setOnClickListener {
            if (currentSize > 8) {
                currentSize--
                updateSizeText()
                Prefs.setUiClockSize(this, currentSize)
                Prefs.setClockSize(currentSize)
            }
        }

        btnPlus.setOnClickListener {
            if (currentSize < 40) {
                currentSize++
                updateSizeText()
                Prefs.setUiClockSize(this, currentSize)
                Prefs.setClockSize(currentSize)
            }
        }
    }
}