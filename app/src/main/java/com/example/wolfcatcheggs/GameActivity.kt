package com.example.wolfcatcheggs

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Random

class GameActivity : AppCompatActivity() {

    private lateinit var tvScore: TextView
    private lateinit var tvGameStatus: TextView
    private lateinit var btnCatch: Button
    private lateinit var btnFinishGame: Button

    private var score = 0
    private var eggsCaught = 0
    private var eggsMissed = 0
    private val random = Random()

    private lateinit var prefs: SharedPreferences
    private lateinit var vibrator: Vibrator
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        initViews()

        prefs = getSharedPreferences("com.example.wolfcatcheggs_preferences", MODE_PRIVATE)

        // Инициализация вибратора (современный способ)
        vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        setupListeners()
        startGame()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initViews() {
        tvScore = findViewById(R.id.tvScore)
        tvGameStatus = findViewById(R.id.tvGameStatus)
        btnCatch = findViewById(R.id.btnCatch)
        btnFinishGame = findViewById(R.id.btnFinishGame)
    }

    private fun setupListeners() {
        btnCatch.setOnClickListener {
            catchEgg()
        }

        btnFinishGame.setOnClickListener {
            finishGame()
        }
    }

    private fun startGame() {
        score = 0
        eggsCaught = 0
        eggsMissed = 0
        updateUI()
        tvGameStatus.text = getString(R.string.game_started)

        // Запускаем имитацию падения яиц
        startFallingEggs()
    }

    private fun startFallingEggs() {
        // Имитация: каждые 2 секунды "падает" яйцо
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (eggsCaught + eggsMissed < 20) { // Максимум 20 яиц за игру
                    tvGameStatus.text = getString(R.string.egg_falling)
                    handler.postDelayed(this, 2000)
                } else {
                    tvGameStatus.text = getString(R.string.no_more_eggs)
                    btnCatch.isEnabled = false
                }
            }
        }, 2000)
    }

    private fun catchEgg() {
        val caught = random.nextBoolean() // 50% шанс поймать

        if (caught) {
            eggsCaught++

            // Очки зависят от сложности
            val difficulty = prefs.getString("difficulty", "medium")
            val points = when (difficulty) {
                "easy" -> 10
                "hard" -> 5
                else -> 7
            }

            score += points
            tvGameStatus.text = getString(R.string.caught_success, points)

            // Вибрация при успехе (если включена)
            if (prefs.getBoolean("vibration_enabled", true)) {
                vibrate(50)
            }
        } else {
            eggsMissed++
            tvGameStatus.text = getString(R.string.caught_miss)

            // Вибрация при промахе
            if (prefs.getBoolean("vibration_enabled", true)) {
                vibrate(100)
            }
        }

        updateUI()
    }

    private fun updateUI() {
        tvScore.text = getString(R.string.score_format, score)
    }

    private fun vibrate(duration: Long) {
        // Проверяем наличие разрешения (хотя оно normal, но проверка не помешает)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }

    private fun finishGame() {
        // Сохраняем рекорд
        saveRecord()

        // Сохраняем статистику игр
        saveStatistics()

        Toast.makeText(this, getString(R.string.game_finished, score), Toast.LENGTH_LONG).show()
        finish()
    }

    private fun saveRecord() {
        val playerName = prefs.getString("player_name", getString(R.string.default_player_name)) ?: getString(R.string.default_player_name)
        val currentRecord = prefs.getInt("record_score", 0)

        if (score > currentRecord) {
            // Новый рекорд!
            prefs.edit {
                putInt("record_score", score)
                putString("record_holder", playerName)
            }

            Toast.makeText(this, R.string.new_record, Toast.LENGTH_LONG).show()
        }
    }

    private fun saveStatistics() {
        val gamesPlayed = prefs.getInt("games_played", 0)
        val totalEggsCaught = prefs.getInt("total_eggs_caught", 0)

        prefs.edit {
            putInt("games_played", gamesPlayed + 1)
            putInt("total_eggs_caught", totalEggsCaught + eggsCaught)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}