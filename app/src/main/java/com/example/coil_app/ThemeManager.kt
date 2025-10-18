package com.example.coil_app

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatActivity

class ThemeManager(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "ThemePrefs"
        private const val KEY_THEME = "selected_theme"

        const val THEME_LIGHT = 0
        const val THEME_DARK = 1
        const val THEME_SYSTEM = 2
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveTheme(themeMode: Int) {
        sharedPreferences.edit()
            .putInt(KEY_THEME, themeMode)
            .apply()
    }

    fun getSavedTheme(): Int {
        return sharedPreferences.getInt(KEY_THEME, THEME_SYSTEM)
    }

    fun applyTheme(themeMode: Int) {
        val mode = when (themeMode) {
            THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            THEME_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun applySavedTheme() {
        applyTheme(getSavedTheme())
    }

    fun getThemeName(themeMode: Int): String {
        return when (themeMode) {
            THEME_LIGHT -> context.getString(R.string.theme_light)
            THEME_DARK -> context.getString(R.string.theme_dark)
            THEME_SYSTEM -> context.getString(R.string.theme_system)
            else -> context.getString(R.string.theme_system)
        }
    }
}

// Extensión para AppCompatActivity
fun AppCompatActivity.applyTheme(themeMode: Int) {
    ThemeManager(this).applyTheme(themeMode)
}

fun AppCompatActivity.applySavedTheme() {
    ThemeManager(this).applySavedTheme()
}