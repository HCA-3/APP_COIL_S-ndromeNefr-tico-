package com.example.coil_app

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Aplicar el tema guardado antes de crear la actividad
        ThemeManager(this).applySavedTheme()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar los botones de idioma
        setupLanguageButtons()
    }

    private fun setupLanguageButtons() {
        val btnSpanish = findViewById<Button>(R.id.btn_spanish)
        val btnEnglish = findViewById<Button>(R.id.btn_english)

        btnSpanish.setOnClickListener {
            setLanguage("es")
        }

        btnEnglish.setOnClickListener {
            setLanguage("en")
        }
    }

    private fun setLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Guardar preferencia de idioma
        val prefs = getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("selected_language", languageCode).apply()

        // Mostrar mensaje y recrear la actividad para aplicar el idioma
        val message = if (languageCode == "es") {
            "Idioma cambiado a Español"
        } else {
            "Language changed to English"
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        // Recrear la actividad para aplicar el cambio de idioma
        recreate()
    }

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

    override fun onResume() {
        super.onResume()
        // Verificar si ya se seleccionó un idioma y redirigir a login
        val prefs = getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)
        val languageCode = prefs.getString("selected_language", null)

        if (languageCode != null) {
            // Ya se seleccionó idioma, ir a login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}