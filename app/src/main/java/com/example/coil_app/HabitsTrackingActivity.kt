package com.example.coil_app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import org.json.JSONObject

class HabitsTrackingActivity : BaseActivity() {

    private lateinit var btnComplete: Button
    private lateinit var sharedPreferences: SharedPreferences

    // RadioGroups para todas las preguntas
    private lateinit var rgEdema: RadioGroup
    private lateinit var rgUrine: RadioGroup
    private lateinit var rgFatigue: RadioGroup
    private lateinit var rgInfections: RadioGroup
    private lateinit var rgBloodPressure: RadioGroup
    private lateinit var rgWater: RadioGroup
    private lateinit var rgSalt: RadioGroup
    private lateinit var rgExercise: RadioGroup
    private lateinit var rgMedications: RadioGroup
    private lateinit var rgChronic: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habits_tracking)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        btnComplete = findViewById(R.id.btn_complete_survey)

        // Inicializar todos los RadioGroups
        rgEdema = findViewById(R.id.rg_edema)
        rgUrine = findViewById(R.id.rg_urine)
        rgFatigue = findViewById(R.id.rg_fatigue)
        rgInfections = findViewById(R.id.rg_infections)
        rgBloodPressure = findViewById(R.id.rg_blood_pressure)
        rgWater = findViewById(R.id.rg_water)
        rgSalt = findViewById(R.id.rg_salt)
        rgExercise = findViewById(R.id.rg_exercise)
        rgMedications = findViewById(R.id.rg_medications)
        rgChronic = findViewById(R.id.rg_chronic)
    }

    private fun setupListeners() {
        btnComplete.setOnClickListener {
            if (validateAllQuestions()) {
                completeSurvey()
            } else {
                Toast.makeText(this, "Por favor responde todas las preguntas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateAllQuestions(): Boolean {
        return rgEdema.checkedRadioButtonId != -1 &&
                rgUrine.checkedRadioButtonId != -1 &&
                rgFatigue.checkedRadioButtonId != -1 &&
                rgInfections.checkedRadioButtonId != -1 &&
                rgBloodPressure.checkedRadioButtonId != -1 &&
                rgWater.checkedRadioButtonId != -1 &&
                rgSalt.checkedRadioButtonId != -1 &&
                rgExercise.checkedRadioButtonId != -1 &&
                rgMedications.checkedRadioButtonId != -1 &&
                rgChronic.checkedRadioButtonId != -1
    }

    private fun completeSurvey() {
        try {
            // Guardar datos completos de la encuesta
            saveCompleteSurveyData()

            Toast.makeText(this, "Evaluación completada exitosamente", Toast.LENGTH_LONG).show()

            // Pequeña pausa para asegurar que los datos se guarden completamente
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                try {
                    android.util.Log.d("HabitsTracking", "Iniciando navegación a SurveyResultsActivity")

                    // Ir a la pantalla de resultados
                    val intent = Intent(this, SurveyResultsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)

                    android.util.Log.d("HabitsTracking", "Navegación iniciada correctamente")
                    finish()

                } catch (e: Exception) {
                    android.util.Log.e("HabitsTracking", "Error en navegación: ${e.message}")
                    e.printStackTrace()
                    Toast.makeText(this, "Error al mostrar resultados. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
                }
            }, 500) // 500ms de retraso para asegurar guardado completo

        } catch (e: Exception) {
            android.util.Log.e("HabitsTracking", "Error completando encuesta: ${e.message}")
            e.printStackTrace()
            Toast.makeText(this, "Error al completar la evaluación. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCompleteSurveyData() {
        try {
            // Crear objeto JSON con todas las respuestas
            val surveyData = JSONObject().apply {
                put("date", java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                    .format(java.util.Date()))

                // Respuestas de síntomas
                put("q1_edema", getRadioText(rgEdema.checkedRadioButtonId))
                put("q2_urine", getRadioText(rgUrine.checkedRadioButtonId))
                put("q3_fatigue", getRadioText(rgFatigue.checkedRadioButtonId))
                put("q4_recent_infections", getRadioText(rgInfections.checkedRadioButtonId))
                put("q5_blood_pressure", getRadioText(rgBloodPressure.checkedRadioButtonId))

                // Respuestas de hábitos
                put("q6_water_intake", getRadioText(rgWater.checkedRadioButtonId))
                put("q7_salt_intake", getRadioText(rgSalt.checkedRadioButtonId))
                put("q8_exercise", getRadioText(rgExercise.checkedRadioButtonId))
                put("q9_medications", getRadioText(rgMedications.checkedRadioButtonId))
                put("q10_chronic_conditions", getRadioText(rgChronic.checkedRadioButtonId))

                put("completedAt", System.currentTimeMillis())
            }

            val surveyDataString = surveyData.toString()
            android.util.Log.d("HabitsTracking", "Guardando datos de encuesta: $surveyDataString")

            // Guardar en UserPrefs para que SurveyResultsActivity pueda acceder
            val editor = sharedPreferences.edit()
            editor.putString("lastSurveyData", surveyDataString)

            // Guardar en historial de encuestas
            val surveyHistory = sharedPreferences.getStringSet("surveyHistory", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
            surveyHistory.add(surveyDataString)

            // Limitar el historial a las últimas 50 evaluaciones
            if (surveyHistory.size > 50) {
                val sortedHistory: List<String> = surveyHistory.map { JSONObject(it) }
                    .sortedByDescending { it.optLong("completedAt", 0L) }
                    .take(50)
                    .map { it.toString() }
                editor.putStringSet("surveyHistory", sortedHistory.toMutableSet())
            } else {
                editor.putStringSet("surveyHistory", surveyHistory)
            }

            // Usar commit() para asegurar guardado inmediato
            val success = editor.commit()
            android.util.Log.d("HabitsTracking", "Datos guardados exitosamente: $success")

        } catch (e: Exception) {
            android.util.Log.e("HabitsTracking", "Error guardando datos de encuesta: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun getRadioText(checkedId: Int): String {
        return when (checkedId) {
            // Edema
            R.id.rb_no_edema -> "No"
            R.id.rb_mild_edema -> "Leve"
            R.id.rb_moderate_edema -> "Moderado/a"
            R.id.rb_severe_edema -> "Severo/a"

            // Orina
            R.id.rb_normal_urine -> "Normal"
            R.id.rb_dark_urine -> "Orina oscura/color té"
            R.id.rb_bloody -> "Con sangre"
            R.id.rb_foamy -> "Espumosa"

            // Fatiga
            R.id.rb_no_fatigue -> "No"
            R.id.rb_mild_fatigue -> "Leve"
            R.id.rb_moderate_fatigue -> "Moderado/a"
            R.id.rb_severe_fatigue -> "Severo/a"

            // Infecciones
            R.id.rb_no_infections -> "No"
            R.id.rb_throat_infection -> "Infección de garganta"
            R.id.rb_skin_infection -> "Infección de piel"
            R.id.rb_other_infection -> "Otra"

            // Presión arterial
            R.id.rb_normal_bp -> "Normal"
            R.id.rb_high_bp -> "Alta"
            R.id.rb_dont_know_bp -> "No lo sé"

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
}