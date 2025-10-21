package com.example.coil_app

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ProfileActivity : BaseActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profileBirthDate: TextView
    private lateinit var profilePhone: TextView
    private lateinit var profileMemberSince: TextView
    private lateinit var optionEditProfile: LinearLayout
    private lateinit var optionChangePassword: LinearLayout
    private lateinit var optionThemeSettings: LinearLayout
    private lateinit var optionSurveyResults: LinearLayout
    private lateinit var optionSurveyHistory: LinearLayout
    private lateinit var btnLogout: AppCompatButton

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

        setContentView(R.layout.activity_profile)

        initViews()
        setupToolbar()
        loadUserData()
        setupClickListeners()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        profileName = findViewById(R.id.profile_name)
        profileEmail = findViewById(R.id.profile_email)
        profileBirthDate = findViewById(R.id.profile_birth_date)
        profilePhone = findViewById(R.id.profile_phone)
        profileMemberSince = findViewById(R.id.profile_member_since)
        optionEditProfile = findViewById(R.id.option_edit_profile)
        optionChangePassword = findViewById(R.id.option_change_password)
        optionThemeSettings = findViewById(R.id.option_theme_settings)
        optionSurveyResults = findViewById(R.id.option_survey_results)
        optionSurveyHistory = findViewById(R.id.option_survey_history)
        btnLogout = findViewById(R.id.btn_logout)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Mi Perfil"

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadUserData() {
        try {
            val userName = sharedPreferences.getString("userName", "Usuario")
            val userEmail = sharedPreferences.getString("userEmail", "usuario@ejemplo.com")
            val userBirthDate = sharedPreferences.getString("userBirthDate", "")
            val userPhone = sharedPreferences.getString("userPhone", "")
            val registrationDate = sharedPreferences.getString("registrationDate", "")

            profileName.text = userName ?: "Usuario"
            profileEmail.text = userEmail ?: "usuario@ejemplo.com"

            if (userBirthDate.isNullOrEmpty()) {
                profileBirthDate.text = getString(R.string.birth_date_info)
            } else {
                profileBirthDate.text = "Fecha de nacimiento: $userBirthDate"
            }

            if (userPhone.isNullOrEmpty()) {
                profilePhone.text = getString(R.string.phone_info)
            } else {
                profilePhone.text = "Teléfono: $userPhone"
            }

            if (registrationDate.isNullOrEmpty()) {
                profileMemberSince.text = getString(R.string.member_since_info)
            } else {
                profileMemberSince.text = "Miembro desde: $registrationDate"
            }
        } catch (e: Exception) {
            android.util.Log.e("ProfileActivity", "Error cargando datos del usuario: ${e.message}")
            Toast.makeText(this, "Error al cargar los datos del perfil", Toast.LENGTH_SHORT).show()

            // Establecer valores por defecto en caso de error
            profileName.text = "Usuario"
            profileEmail.text = "usuario@ejemplo.com"
            profileBirthDate.text = getString(R.string.birth_date_info)
            profilePhone.text = getString(R.string.phone_info)
            profileMemberSince.text = getString(R.string.member_since_info)
        }
    }

    private fun setupClickListeners() {
        try {
            optionEditProfile.setOnClickListener {
                showEditProfileDialog()
            }

            optionChangePassword.setOnClickListener {
                showChangePasswordDialog()
            }

            optionThemeSettings.setOnClickListener {
                showThemeSelectorDialog()
            }

            optionSurveyResults.setOnClickListener {
                try {
                    startActivity(Intent(this, SurveyResultsActivity::class.java))
                } catch (e: Exception) {
                    android.util.Log.e("ProfileActivity", "Error abriendo resultados: ${e.message}")
                    Toast.makeText(this, "Error al abrir resultados", Toast.LENGTH_SHORT).show()
                }
            }

            optionSurveyHistory.setOnClickListener {
                try {
                    startActivity(Intent(this, SurveyHistoryActivity::class.java))
                } catch (e: Exception) {
                    android.util.Log.e("ProfileActivity", "Error abriendo historial: ${e.message}")
                    Toast.makeText(this, "Error al abrir historial", Toast.LENGTH_SHORT).show()
                }
            }

            btnLogout.setOnClickListener {
                showLogoutConfirmationDialog()
            }
        } catch (e: Exception) {
            android.util.Log.e("ProfileActivity", "Error configurando listeners: ${e.message}")
            Toast.makeText(this, "Error al configurar el perfil", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showEditProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.et_name)
        val etBirthDate = dialogView.findViewById<TextInputEditText>(R.id.et_birth_date)
        val etPhone = dialogView.findViewById<TextInputEditText>(R.id.et_phone)

        // Cargar datos actuales
        etName.setText(sharedPreferences.getString("userName", ""))
        etBirthDate.setText(sharedPreferences.getString("userBirthDate", ""))
        etPhone.setText(sharedPreferences.getString("userPhone", ""))

        val dialog = AlertDialog.Builder(this)
            .setTitle("Editar Perfil")
            .setView(dialogView)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()

        // Configurar el botón positivo para validar antes de guardar
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = etName.text.toString().trim()
            val birthDate = etBirthDate.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            if (name.isEmpty()) {
                etName.error = "El nombre es obligatorio"
                return@setOnClickListener
            }

            // Guardar datos
            val editor = sharedPreferences.edit()
            editor.putString("userName", name)
            editor.putString("userBirthDate", birthDate)
            editor.putString("userPhone", phone)
            editor.apply()

            // Actualizar UI
            loadUserData()

            Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val etCurrentPassword = dialogView.findViewById<TextInputEditText>(R.id.et_current_password)
        val etNewPassword = dialogView.findViewById<TextInputEditText>(R.id.et_new_password)
        val etConfirmPassword = dialogView.findViewById<TextInputEditText>(R.id.et_confirm_password)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Cambiar Contraseña")
            .setView(dialogView)
            .setPositiveButton("Cambiar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val currentPassword = etCurrentPassword.text.toString()
            val newPassword = etNewPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            val savedPassword = sharedPreferences.getString("userPassword", "")

            if (currentPassword.isEmpty()) {
                etCurrentPassword.error = "Ingresa tu contraseña actual"
                return@setOnClickListener
            }

            if (newPassword.length < 6) {
                etNewPassword.error = "La contraseña debe tener al menos 6 caracteres"
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                etConfirmPassword.error = "Las contraseñas no coinciden"
                return@setOnClickListener
            }

            if (currentPassword != savedPassword) {
                etCurrentPassword.error = "Contraseña actual incorrecta"
                return@setOnClickListener
            }

            // Guardar nueva contraseña
            val editor = sharedPreferences.edit()
            editor.putString("userPassword", newPassword)
            editor.apply()

            Toast.makeText(this, "Contraseña cambiada correctamente", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que quieres cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                // Limpiar sesión
                val editor = sharedPreferences.edit()
                editor.putBoolean("isLoggedIn", false)
                editor.apply()

                // Ir a pantalla de login
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showThemeSelectorDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_theme_selector, null)

        val optionLightTheme = dialogView.findViewById<LinearLayout>(R.id.option_light_theme)
        val optionDarkTheme = dialogView.findViewById<LinearLayout>(R.id.option_dark_theme)
        val optionSystemTheme = dialogView.findViewById<LinearLayout>(R.id.option_system_theme)

        val rbLightTheme = dialogView.findViewById<android.widget.RadioButton>(R.id.rb_light_theme)
        val rbDarkTheme = dialogView.findViewById<android.widget.RadioButton>(R.id.rb_dark_theme)
        val rbSystemTheme = dialogView.findViewById<android.widget.RadioButton>(R.id.rb_system_theme)

        val themeManager = ThemeManager(this)
        val currentTheme = themeManager.getSavedTheme()

        // Marcar el tema actual
        when (currentTheme) {
            ThemeManager.THEME_LIGHT -> rbLightTheme.isChecked = true
            ThemeManager.THEME_DARK -> rbDarkTheme.isChecked = true
            ThemeManager.THEME_SYSTEM -> rbSystemTheme.isChecked = true
        }

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.theme_settings))
            .setView(dialogView)
            .setPositiveButton("Aceptar") { _, _ ->
                val selectedTheme = when {
                    rbLightTheme.isChecked -> ThemeManager.THEME_LIGHT
                    rbDarkTheme.isChecked -> ThemeManager.THEME_DARK
                    rbSystemTheme.isChecked -> ThemeManager.THEME_SYSTEM
                    else -> currentTheme
                }

                themeManager.saveTheme(selectedTheme)
                themeManager.applyTheme(selectedTheme)

                Toast.makeText(this, getString(R.string.theme_changed), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()

        // Configurar clics en las opciones
        optionLightTheme.setOnClickListener {
            rbLightTheme.isChecked = true
            rbDarkTheme.isChecked = false
            rbSystemTheme.isChecked = false
        }

        optionDarkTheme.setOnClickListener {
            rbLightTheme.isChecked = false
            rbDarkTheme.isChecked = true
            rbSystemTheme.isChecked = false
        }

        optionSystemTheme.setOnClickListener {
            rbLightTheme.isChecked = false
            rbDarkTheme.isChecked = false
            rbSystemTheme.isChecked = true
        }
    }
}