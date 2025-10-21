package com.example.coil_app

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*

open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        val prefs = newBase?.getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)
        val languageCode = prefs?.getString("selected_language", null)

        val context = if (languageCode != null) {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)
            newBase.createConfigurationContext(config)
        } else {
            newBase
        }

        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Aplicar el tema guardado antes de crear la actividad
        ThemeManager(this).applySavedTheme()

        super.onCreate(savedInstanceState)

        // Aplicar el idioma guardado al crear la actividad
        applyLanguage()
    }

    private fun applyLanguage() {
        try {
            val prefs = getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)
            val languageCode = prefs.getString("selected_language", null)

            if (languageCode != null) {
                val locale = Locale(languageCode)
                Locale.setDefault(locale)

                val config = Configuration()
                config.setLocale(locale)
                resources.updateConfiguration(config, resources.displayMetrics)
            }
        } catch (e: Exception) {
            // Si hay un error con el idioma, continuar con el idioma por defecto
            e.printStackTrace()
        }
    }
}