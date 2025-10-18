package com.example.coil_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvNoAccount: TextView
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar Firebase Auth
        auth = Firebase.auth

        // Inicializar vistas
        initViews()

        // Configurar listeners
        setupListeners()

        // Verificar si ya hay un usuario logueado
        checkCurrentUser()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        progressBar = findViewById(R.id.progress_bar)
        tvForgotPassword = findViewById(R.id.tv_forgot_password)
        tvNoAccount = findViewById(R.id.tv_no_account)
        tvRegister = findViewById(R.id.tv_register)
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener {
            if (validateForm()) {
                loginUser()
            }
        }

        tvForgotPassword.setOnClickListener {
            // TODO: Implementar recuperación de contraseña
            Toast.makeText(this, "Password recovery coming soon", Toast.LENGTH_SHORT).show()
        }

        tvRegister.setOnClickListener {
            // Ir a la pantalla de registro
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkCurrentUser() {
        // Verificar si hay un usuario logueado localmente
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // Usuario ya está logueado, ir a la pantalla principal
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Validar email
        val email = etEmail.text.toString().trim()
        if (email.isEmpty()) {
            etEmail.error = getString(R.string.error_empty_field)
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = getString(R.string.error_invalid_email)
            isValid = false
        } else {
            etEmail.error = null
        }

        // Validar contraseña
        val password = etPassword.text.toString()
        if (password.isEmpty()) {
            etPassword.error = getString(R.string.error_empty_field)
            isValid = false
        } else {
            etPassword.error = null
        }

        return isValid
    }

    private fun loginUser() {
        // COMENTADO TEMPORALMENTE - Deshabilitar login para probar otras funcionalidades
        /*
        progressBar.visibility = ProgressBar.VISIBLE
        btnLogin.isEnabled = false

        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login exitoso
                    progressBar.visibility = ProgressBar.GONE
                    btnLogin.isEnabled = true
                    Toast.makeText(
                        baseContext,
                        getString(R.string.login_success),
                        Toast.LENGTH_SHORT
                    ).show()

                    // Ir a la pantalla principal
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Error en el login
                    progressBar.visibility = ProgressBar.GONE
                    btnLogin.isEnabled = true
                    Toast.makeText(
                        baseContext,
                        getString(R.string.error_login_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        */

        // TEMPORAL: Login local para probar funcionalidades
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        if (authenticateUserLocally(email, password)) {
            // Login exitoso
            val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("isLoggedIn", true)
            editor.apply()

            Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()

            // Ir a la pantalla principal
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Error en el login
            Toast.makeText(this, getString(R.string.error_login_failed), Toast.LENGTH_SHORT).show()
        }
    }

    private fun authenticateUserLocally(email: String, password: String): Boolean {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("userEmail", "")
        val savedPassword = sharedPreferences.getString("userPassword", "")

        return email == savedEmail && password == savedPassword
    }

    override fun onStart() {
        super.onStart()
        // Verificar si el usuario ya está logueado
        checkCurrentUser()
    }
}