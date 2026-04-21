package com.example.wolfcatcheggs

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RecordsActivity : AppCompatActivity() {

    private lateinit var tvRecordInfo: TextView
    private lateinit var tvGameHistory: TextView
    private lateinit var btnClearRecords: Button
    private lateinit var btnBackFromRecords: Button

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records)

        initViews()

        prefs = getSharedPreferences("com.example.wolfcatcheggs_preferences", MODE_PRIVATE)

        setupListeners()
        loadRecords()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initViews() {
        tvRecordInfo = findViewById(R.id.tvRecordInfo)
        tvGameHistory = findViewById(R.id.tvGameHistory)
        btnClearRecords = findViewById(R.id.btnClearRecords)
        btnBackFromRecords = findViewById(R.id.btnBackFromRecords)
    }

    private fun setupListeners() {
        btnClearRecords.setOnClickListener {
            clearRecords()
        }

        btnBackFromRecords.setOnClickListener {
            finish()
        }
    }

    private fun loadRecords() {
        val recordScore = prefs.getInt("record_score", 0)
        val recordHolder = prefs.getString("record_holder", "Никто") ?: "Никто"

        val recordText = """
            🥇 Рекордсмен: $recordHolder
            🎯 Лучший счёт: $recordScore
        """.trimIndent()

        tvRecordInfo.text = recordText

        // Загружаем историю игр
        val gamesPlayed = prefs.getInt("games_played", 0)
        val totalEggsCaught = prefs.getInt("total_eggs_caught", 0)

        val historyText = """
            🎮 Сыграно игр: $gamesPlayed
            🥚 Всего поймано яиц: $totalEggsCaught
        """.trimIndent()

        tvGameHistory.text = historyText
    }

    private fun clearRecords() {
        prefs.edit().apply {
            remove("record_score")
            remove("record_holder")
            remove("games_played")
            remove("total_eggs_caught")
            apply()
        }

        Toast.makeText(this, "Рекорды сброшены", Toast.LENGTH_SHORT).show()
        loadRecords()
    }
}