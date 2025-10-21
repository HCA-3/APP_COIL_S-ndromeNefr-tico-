package com.example.coil_app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton

class HomeActivity : BaseActivity() {

    private lateinit var btnNephrotic: Button
    private lateinit var btnGlomerulonephritis: Button
    private lateinit var btnHabits: Button
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var btnProfile: AppCompatButton

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Verificar si el usuario está logueado
        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            // Si no está logueado, redirigir al login
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_home)

        initViews()
        loadUserData()
        setupListeners()
    }

    private fun initViews() {
        btnNephrotic = findViewById(R.id.btn_nephrotic)
        btnGlomerulonephritis = findViewById(R.id.btn_glomerulonephritis)
        btnHabits = findViewById(R.id.btn_habits)
        userName = findViewById(R.id.user_name)
        userEmail = findViewById(R.id.user_email)
        btnProfile = findViewById(R.id.btn_profile)
    }

    private fun loadUserData() {
        val name = sharedPreferences.getString("userName", "Usuario")
        val email = sharedPreferences.getString("userEmail", "usuario@ejemplo.com")

        userName.text = "¡Bienvenido, $name!"
        userEmail.text = email
    }

    private fun setupListeners() {
        btnNephrotic.setOnClickListener {
            startActivity(Intent(this, NephroticInfoActivity::class.java))
        }

        btnGlomerulonephritis.setOnClickListener {
            startActivity(Intent(this, GlomerulonephritisInfoActivity::class.java))
        }

        btnHabits.setOnClickListener {
            startActivity(Intent(this, HabitsTrackingActivity::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}