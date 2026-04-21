package com.example.wolfcatcheggs

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvCurrentSettings: TextView
    private lateinit var btnPlay: Button
    private lateinit var btnSettings: Button
    private lateinit var btnRecords: Button

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

        prefs = getSharedPreferences("com.example.wolfcatcheggs_preferences", MODE_PRIVATE)

        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        updateSettingsDisplay()
    }

    private fun initViews() {
        tvCurrentSettings = findViewById(R.id.tvCurrentSettings)
        btnPlay = findViewById(R.id.btnPlay)
        btnSettings = findViewById(R.id.btnSettings)
        btnRecords = findViewById(R.id.btnRecords)
    }

    private fun setupListeners() {
        btnPlay.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        btnRecords.setOnClickListener {
            startActivity(Intent(this, RecordsActivity::class.java))
        }
    }

    private fun updateSettingsDisplay() {
        val playerName = prefs.getString("player_name", getString(R.string.not_specified))
        val difficulty = when (prefs.getString("difficulty", "medium")) {
            "easy" -> "Легкая"
            "hard" -> "Сложная"
            else -> "Средняя"
        }
        val soundEnabled = prefs.getBoolean("sound_enabled", true)

        val settingsText = """
            👤 Игрок: $playerName
            📊 Сложность: $difficulty
            🔊 Звук: ${if (soundEnabled) getString(R.string.sound_on) else getString(R.string.sound_off)}
        """.trimIndent()

        tvCurrentSettings.text = settingsText
    }
}