package com.example.coil_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import org.json.JSONObject


class HabitsTrackingPart2Activity : BaseActivity() {

    private lateinit var btnBack: Button
    private lateinit var btnComplete: Button
    private lateinit var rgWater: RadioGroup
    private lateinit var rgSalt: RadioGroup
    private lateinit var rgExercise: RadioGroup
    private lateinit var rgMedications: RadioGroup
    private lateinit var rgChronic: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habits_tracking_part2)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btn_back)
        btnComplete = findViewById(R.id.btn_complete_survey)
        rgWater = findViewById(R.id.rg_water)
        rgSalt = findViewById(R.id.rg_salt)
        rgExercise = findViewById(R.id.rg_exercise)
        rgMedications = findViewById(R.id.rg_medications)
        rgChronic = findViewById(R.id.rg_chronic)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnComplete.setOnClickListener {
            if (validatePart2()) {
                // Guardar respuestas de la parte 2
                savePart2Answers()

                // Completar encuesta y mostrar resultados
                completeSurvey()
            } else {
                Toast.makeText(this, "Por favor responde todas las preguntas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validatePart2(): Boolean {
        return rgWater.checkedRadioButtonId != -1 &&
                rgSalt.checkedRadioButtonId != -1 &&
                rgExercise.checkedRadioButtonId != -1 &&
                rgMedications.checkedRadioButtonId != -1 &&
                rgChronic.checkedRadioButtonId != -1
    }

    private fun savePart2Answers() {
        val prefs = getSharedPreferences("SurveyAnswers", MODE_PRIVATE)
        val editor = prefs.edit()

        // Guardar IDs de radio buttons para referencia
        editor.putInt("water", rgWater.checkedRadioButtonId)
        editor.putInt("salt", rgSalt.checkedRadioButtonId)
        editor.putInt("exercise", rgExercise.checkedRadioButtonId)
        editor.putInt("medications", rgMedications.checkedRadioButtonId)
        editor.putInt("chronic", rgChronic.checkedRadioButtonId)

        // Guardar respuestas como texto para fácil acceso
        editor.putString("q6_water_intake", getRadioText(rgWater.checkedRadioButtonId))
        editor.putString("q7_salt_intake", getRadioText(rgSalt.checkedRadioButtonId))
        editor.putString("q8_exercise", getRadioText(rgExercise.checkedRadioButtonId))
        editor.putString("q9_medications", getRadioText(rgMedications.checkedRadioButtonId))
        editor.putString("q10_chronic_conditions", getRadioText(rgChronic.checkedRadioButtonId))

        editor.putLong("completedAt", System.currentTimeMillis())

        editor.apply()
    }

    private fun getRadioText(checkedId: Int): String {
        return when (checkedId) {
            // Agua
            R.id.rb_less_1l -> "Menos de 1 litro"
            R.id.rb_1_2l -> "1-2 litros"
            R.id.rb_more_2l -> "Más de 2 litros"

            // Sal
            R.id.rb_low_salt -> "Bajo"
            R.id.rb_moderate_salt -> "Moderado"
            R.id.rb_high_salt -> "Alto"

            // Ejercicio
            R.id.rb_never_exercise -> "Nunca"
            R.id.rb_1_2_weekly -> "1-2 veces por semana"
            R.id.rb_3_plus_weekly -> "3+ veces por semana"

            // Medicamentos
            R.id.rb_no_meds -> "No tomo medicamentos"
            R.id.rb_bp_meds -> "Medicamentos para presión arterial"
            R.id.rb_pain_meds -> "Analgésicos/antiinflamatorios"
            R.id.rb_other_meds -> "Otros medicamentos"

            // Condiciones crónicas
            R.id.rb_none_chronic -> "Ninguna"
            R.id.rb_diabetes -> "Diabetes"
            R.id.rb_hypertension -> "Hipertensión"
            R.id.rb_autoimmune -> "Enfermedad autoinmune"

            else -> "Respuesta seleccionada"
        }
    }

    private fun completeSurvey() {
        // Guardar datos completos de la encuesta en formato JSON
        saveCompleteSurveyData()

        Toast.makeText(this, "Evaluación completada exitosamente", Toast.LENGTH_LONG).show()

        // Ir a la pantalla de resultados
        val intent = Intent(this, SurveyResultsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveCompleteSurveyData() {
        val prefs = getSharedPreferences("SurveyAnswers", MODE_PRIVATE)
        val userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Crear objeto JSON con todas las respuestas
        val surveyData = org.json.JSONObject().apply {
            put("date", java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date()))

            // Respuestas de la parte 1
            put("q1_edema", prefs.getString("q1_edema", "No respondido"))
            put("q2_urine", prefs.getString("q2_urine", "No respondido"))
            put("q3_fatigue", prefs.getString("q3_fatigue", "No respondido"))
            put("q4_recent_infections", prefs.getString("q4_recent_infections", "No respondido"))
            put("q5_blood_pressure", prefs.getString("q5_blood_pressure", "No respondido"))

            // Respuestas de la parte 2
            put("q6_water_intake", prefs.getString("q6_water_intake", "No respondido"))
            put("q7_salt_intake", prefs.getString("q7_salt_intake", "No respondido"))
            put("q8_exercise", prefs.getString("q8_exercise", "No respondido"))
            put("q9_medications", prefs.getString("q9_medications", "No respondido"))
            put("q10_chronic_conditions", prefs.getString("q10_chronic_conditions", "No respondido"))

            put("completedAt", prefs.getLong("completedAt", System.currentTimeMillis()))
        }

        // Guardar en UserPrefs para que SurveyResultsActivity pueda acceder
        val editor = userPrefs.edit()
        editor.putString("lastSurveyData", surveyData.toString())

        // Guardar en historial de encuestas
        val surveyHistory = userPrefs.getStringSet("surveyHistory", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        surveyHistory.add(surveyData.toString())

        // Limitar el historial a las últimas 50 evaluaciones para no sobrecargar el almacenamiento
        if (surveyHistory.size > 50) {
            val sortedHistory: List<String> = surveyHistory.map { JSONObject(it) }
                .sortedByDescending { it.optLong("completedAt", 0L) }
                .take(50)
                .map { it.toString() }
            editor.putStringSet("surveyHistory", sortedHistory.toMutableSet())
        } else {
            editor.putStringSet("surveyHistory", surveyHistory)
        }

        editor.apply()
    }
}