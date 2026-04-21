package com.example.wolfcatcheggs

import android.content.SharedPreferences
import androidx.core.content.edit

// Функция-расширение для удобной работы с SharedPreferences
inline fun SharedPreferences.edit(block: SharedPreferences.Editor.() -> Unit) {
    edit().apply {
        block()
        apply()
    }
}