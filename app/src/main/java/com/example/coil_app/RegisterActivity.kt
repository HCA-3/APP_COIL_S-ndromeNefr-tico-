package com.example.coil_app

import android.app.DatePickerDialog
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var etFullName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var etBirthDate: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvHaveAccount: TextView

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar Firebase con manejo de errores
        try {
            auth = Firebase.auth
            db = Firebase.firestore
            android.util.Log.d("RegisterActivity", "Firebase inicializado correctamente")
        } catch (e: Exception) {
            android.util.Log.e("RegisterActivity", "Error inicializando Firebase: ${e.message}")
            Toast.makeText(this, "Error de configuración de Firebase: ${e.message}", Toast.LENGTH_LONG).show()
            return
        }

        // Inicializar vistas
        initViews()

        // Configurar listeners
        setupListeners()
    }

    private fun initViews() {
        etFullName = findViewById(R.id.et_full_name)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        etBirthDate = findViewById(R.id.et_birth_date)
        etPhone = findViewById(R.id.et_phone)
        btnRegister = findViewById(R.id.btn_register)
        progressBar = findViewById(R.id.progress_bar)
        tvHaveAccount = findViewById(R.id.tv_have_account)
    }

    private fun setupListeners() {
        btnRegister.setOnClickListener {
            if (validateForm()) {
                registerUser()
            }
        }

        etBirthDate.setOnClickListener {
            showDatePicker()
        }

        tvHaveAccount.setOnClickListener {
            // Ir a la pantalla de login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateInView() {
        val format = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        etBirthDate.setText(sdf.format(calendar.time))
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Validar nombre completo
        if (etFullName.text.toString().trim().isEmpty()) {
            etFullName.error = getString(R.string.error_empty_field)
            isValid = false
        } else {
            etFullName.error = null
        }

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
        } else if (password.length < 6) {
            etPassword.error = getString(R.string.error_password_short)
            isValid = false
        } else {
            etPassword.error = null
        }

        // Validar confirmación de contraseña
        val confirmPassword = etConfirmPassword.text.toString()
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = getString(R.string.error_empty_field)
            isValid = false
        } else if (password != confirmPassword) {
            etConfirmPassword.error = getString(R.string.error_password_mismatch)
            isValid = false
        } else {
            etConfirmPassword.error = null
        }

        // Validar fecha de nacimiento
        if (etBirthDate.text.toString().trim().isEmpty()) {
            etBirthDate.error = getString(R.string.error_empty_field)
            isValid = false
        } else {
            etBirthDate.error = null
        }

        // Validar teléfono
        if (etPhone.text.toString().trim().isEmpty()) {
            etPhone.error = getString(R.string.error_empty_field)
            isValid = false
        } else {
            etPhone.error = null
        }

        return isValid
    }

    private fun registerUser() {
        // COMENTADO TEMPORALMENTE - Deshabilitar registro para probar otras funcionalidades
        /*
        progressBar.visibility = ProgressBar.VISIBLE
        btnRegister.isEnabled = false

        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        // Log para debugging
        android.util.Log.d("RegisterActivity", "Intentando registrar usuario con email: $email")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    android.util.Log.d("RegisterActivity", "Usuario creado exitosamente")
                    // Registro exitoso, guardar datos adicionales en Firestore
                    saveUserToFirestore()
                } else {
                    // Error en el registro - mostrar error específico
                    progressBar.visibility = ProgressBar.GONE
                    btnRegister.isEnabled = true

                    val errorMessage = task.exception?.message ?: "Error desconocido"
                    android.util.Log.e("RegisterActivity", "Error en registro: $errorMessage")

                    Toast.makeText(
                        baseContext,
                        "Error de registro: $errorMessage",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        */

        // TEMPORAL: Guardar datos localmente y simular registro exitoso
        saveUserLocally()

        Toast.makeText(this, "Registro completado exitosamente", Toast.LENGTH_SHORT).show()

        // Ir a la pantalla principal
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveUserLocally() {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Guardar datos del usuario
        editor.putString("userName", etFullName.text.toString().trim())
        editor.putString("userEmail", etEmail.text.toString().trim())
        editor.putString("userPassword", etPassword.text.toString())
        editor.putString("userBirthDate", etBirthDate.text.toString().trim())
        editor.putString("userPhone", etPhone.text.toString().trim())

        // Fecha de registro
        val registrationDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        editor.putString("registrationDate", registrationDate)

        // Marcar como logged in
        editor.putBoolean("isLoggedIn", true)

        editor.apply()
    }

    private fun saveUserToFirestore() {
        // COMENTADO TEMPORALMENTE - Deshabilitar guardado en Firestore
        /*
        val user = auth.currentUser
        if (user != null) {
            android.util.Log.d("RegisterActivity", "Guardando datos en Firestore para usuario: ${user.uid}")

            val userData = hashMapOf(
                "uid" to user.uid,
                "fullName" to etFullName.text.toString().trim(),
                "email" to etEmail.text.toString().trim(),
                "birthDate" to etBirthDate.text.toString().trim(),
                "phone" to etPhone.text.toString().trim(),
                "createdAt" to System.currentTimeMillis(),
                "profileCompleted" to false
            )

            db.collection("users").document(user.uid)
                .set(userData)
                .addOnSuccessListener {
                    android.util.Log.d("RegisterActivity", "Datos guardados exitosamente en Firestore")
                    progressBar.visibility = ProgressBar.GONE
                    btnRegister.isEnabled = true
                    Toast.makeText(
                        this,
                        getString(R.string.registration_success),
                        Toast.LENGTH_SHORT
                    ).show()

                    // Ir a la pantalla principal
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("RegisterActivity", "Error guardando en Firestore: ${e.message}")
                    progressBar.visibility = ProgressBar.GONE
                    btnRegister.isEnabled = true
                    Toast.makeText(
                        this,
                        "Error guardando datos: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        } else {
            android.util.Log.e("RegisterActivity", "Usuario es nulo después del registro")
            progressBar.visibility = ProgressBar.GONE
            btnRegister.isEnabled = true
            Toast.makeText(this, "Error: usuario no creado correctamente", Toast.LENGTH_LONG).show()
        }
        */
    }
}