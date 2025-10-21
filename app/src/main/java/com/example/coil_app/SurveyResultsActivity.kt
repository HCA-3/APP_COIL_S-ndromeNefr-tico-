package com.example.coil_app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class SurveyResultsActivity : BaseActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var surveyDate: TextView
    private lateinit var riskLevel: TextView
    private lateinit var edemaResult: TextView
    private lateinit var urineResult: TextView
    private lateinit var fatigueResult: TextView
    private lateinit var infectionsResult: TextView
    private lateinit var bloodPressureResult: TextView
    private lateinit var waterResult: TextView
    private lateinit var saltResult: TextView
    private lateinit var exerciseResult: TextView
    private lateinit var medicationsResult: TextView
    private lateinit var chronicResult: TextView
    private lateinit var recommendationsText: TextView
    private lateinit var btnNewSurvey: AppCompatButton
    private lateinit var btnShareResults: AppCompatButton
    private lateinit var btnViewHistory: AppCompatButton
    private lateinit var btnGoToMenu: AppCompatButton

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_results)

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        initViews()
        setupToolbar()
        loadSurveyResults()
        setupClickListeners()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        surveyDate = findViewById(R.id.survey_date)
        riskLevel = findViewById(R.id.risk_level)
        edemaResult = findViewById(R.id.edema_result)
        urineResult = findViewById(R.id.urine_result)
        fatigueResult = findViewById(R.id.fatigue_result)
        infectionsResult = findViewById(R.id.infections_result)
        bloodPressureResult = findViewById(R.id.blood_pressure_result)
        waterResult = findViewById(R.id.water_result)
        saltResult = findViewById(R.id.salt_result)
        exerciseResult = findViewById(R.id.exercise_result)
        medicationsResult = findViewById(R.id.medications_result)
        chronicResult = findViewById(R.id.chronic_result)
        recommendationsText = findViewById(R.id.recommendations_text)
        btnNewSurvey = findViewById(R.id.btn_new_survey)
        btnShareResults = findViewById(R.id.btn_share_results)
        btnViewHistory = findViewById(R.id.btn_view_history)
        btnGoToMenu = findViewById(R.id.btn_go_to_menu)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadSurveyResults() {
        val surveyDataJson = sharedPreferences.getString("lastSurveyData", null)

        if (surveyDataJson == null) {
            showNoDataMessage()
            return
        }

        try {
            val surveyData = JSONObject(surveyDataJson)
            val surveyDateStr = surveyData.getString("date")

            surveyDate.text = "Fecha: $surveyDateStr"

            // Analizar respuestas y generar resultados
            analyzeSurveyResults(surveyData)

        } catch (e: Exception) {
            showNoDataMessage()
        }
    }

    private fun analyzeSurveyResults(surveyData: JSONObject) {
        val riskScore = calculateRiskScore(surveyData)
        val riskLevelText = when {
            riskScore >= 8 -> "ALTO"
            riskScore >= 5 -> "MODERADO"
            riskScore >= 2 -> "BAJO"
            else -> "MÍNIMO"
        }

        val riskColor = when {
            riskScore >= 8 -> "#C1121F" // Rojo
            riskScore >= 5 -> "#FF9800" // Naranja
            riskScore >= 2 -> "#669BBC" // Azul
            else -> "#4CAF50" // Verde
        }

        riskLevel.text = "Nivel de riesgo: $riskLevelText"
        riskLevel.setTextColor(android.graphics.Color.parseColor(riskColor))

        // Analizar síntomas
        analyzeSymptoms(surveyData)

        // Analizar hábitos
        analyzeHabits(surveyData)

        // Generar recomendaciones
        generateRecommendations(surveyData, riskScore)
    }

    private fun calculateRiskScore(surveyData: JSONObject): Int {
        var score = 0

        // Síntomas
        if (surveyData.optString("q1_edema") == "Leve") score += 1
        if (surveyData.optString("q1_edema") == "Moderado/a") score += 2
        if (surveyData.optString("q1_edema") == "Severo/a") score += 3

        if (surveyData.optString("q2_urine") == "Orina oscura/color té") score += 2
        if (surveyData.optString("q2_urine") == "Con sangre") score += 3
        if (surveyData.optString("q2_urine") == "Espumosa") score += 2

        if (surveyData.optString("q3_fatigue") == "Moderado/a") score += 1
        if (surveyData.optString("q3_fatigue") == "Severo/a") score += 2

        if (surveyData.optString("q4_recent_infections") != "No") score += 1

        if (surveyData.optString("q5_blood_pressure") == "Alta") score += 2

        // Hábitos de riesgo
        if (surveyData.optString("q6_water_intake") == "Menos de 1 litro") score += 1
        if (surveyData.optString("q7_salt_intake") == "Alto") score += 1
        if (surveyData.optString("q8_exercise") == "Nunca") score += 1

        return score
    }

    private fun analyzeSymptoms(surveyData: JSONObject) {
        val edema = surveyData.optString("q1_edema", "No evaluado")
        edemaResult.text = "Edema: $edema"

        val urineChanges = surveyData.optString("q2_urine", "No evaluado")
        urineResult.text = "Cambios en orina: $urineChanges"

        val fatigue = surveyData.optString("q3_fatigue", "No evaluado")
        fatigueResult.text = "Nivel de fatiga: $fatigue"

        val infections = surveyData.optString("q4_recent_infections", "No evaluado")
        infectionsResult.text = "Infecciones recientes: $infections"

        val bloodPressure = surveyData.optString("q5_blood_pressure", "No evaluado")
        bloodPressureResult.text = "Presión arterial: $bloodPressure"
    }

    private fun analyzeHabits(surveyData: JSONObject) {
        val waterIntake = surveyData.optString("q6_water_intake", "No evaluado")
        waterResult.text = "Consumo de agua: $waterIntake"

        val saltIntake = surveyData.optString("q7_salt_intake", "No evaluado")
        saltResult.text = "Consumo de sal: $saltIntake"

        val exercise = surveyData.optString("q8_exercise", "No evaluado")
        exerciseResult.text = "Frecuencia de ejercicio: $exercise"

        val medications = surveyData.optString("q9_medications", "No evaluado")
        medicationsResult.text = "Medicamentos: $medications"

        val chronic = surveyData.optString("q10_chronic_conditions", "No evaluado")
        chronicResult.text = "Condiciones crónicas: $chronic"
    }

    private fun generateRecommendations(surveyData: JSONObject, riskScore: Int) {
        val recommendations = mutableListOf<String>()

        // Recomendaciones basadas en síntomas
        if (surveyData.optString("q1_edema") != "No") {
            recommendations.add("• Consulta a un médico sobre el edema (hinchazón) lo antes posible")
        }

        if (surveyData.optString("q2_urine") != "No") {
            recommendations.add("• Los cambios en el color o apariencia de la orina requieren evaluación médica")
        }

        if (surveyData.optString("q3_fatigue") == "Severo/a") {
            recommendations.add("• La fatiga severa puede ser un signo de problemas renales, busca atención médica")
        }

        if (surveyData.optString("q4_recent_infections") != "No") {
            recommendations.add("• Las infecciones recientes pueden afectar los riñones, mantente vigilante")
        }

        if (surveyData.optString("q5_blood_pressure") == "Alta") {
            recommendations.add("• Controla tu presión arterial regularmente y sigue el tratamiento médico")
        }

        // Recomendaciones basadas en hábitos
        if (surveyData.optString("q6_water_intake") == "Menos de 1 litro") {
            recommendations.add("• Aumenta tu consumo de agua a al menos 2 litros diarios")
        }

        if (surveyData.optString("q7_salt_intake") == "Alto") {
            recommendations.add("• Reduce el consumo de sal para proteger tus riñones")
        }

        if (surveyData.optString("q8_exercise") == "Nunca") {
            recommendations.add("• Incorpora ejercicio moderado 3 veces por semana")
        }

        // Recomendaciones generales según nivel de riesgo
        when {
            riskScore >= 8 -> {
                recommendations.add("⚠️ NIVEL DE RIESGO ALTO: Se recomienda consulta médica urgente")
                recommendations.add("• Realiza análisis de orina y sangre lo antes posible")
                recommendations.add("• Evita medicamentos que puedan dañar los riñones sin supervisión médica")
            }
            riskScore >= 5 -> {
                recommendations.add("⚠️ NIVEL DE RIESGO MODERADO: Se recomienda consulta médica pronto")
                recommendations.add("• Programa una cita con tu médico para evaluación")
                recommendations.add("• Monitorea tus síntomas diariamente")
            }
            riskScore >= 2 -> {
                recommendations.add("• Mantén un seguimiento regular de tu salud")
                recommendations.add("• Considera realizar chequeos médicos periódicos")
            }
            else -> {
                recommendations.add("• Continúa con buenos hábitos de salud")
                recommendations.add("• Realiza chequeos médicos anuales")
            }
        }

        recommendationsText.text = recommendations.joinToString("\n")
    }

    private fun showNoDataMessage() {
        surveyDate.text = getString(R.string.survey_date_placeholder)
        riskLevel.text = getString(R.string.risk_level_placeholder)
        edemaResult.text = getString(R.string.edema_analysis)
        urineResult.text = getString(R.string.urine_analysis)
        fatigueResult.text = getString(R.string.fatigue_analysis)
        infectionsResult.text = getString(R.string.infections_analysis)
        bloodPressureResult.text = getString(R.string.blood_pressure_analysis)
        waterResult.text = getString(R.string.water_analysis)
        saltResult.text = getString(R.string.salt_analysis)
        exerciseResult.text = getString(R.string.exercise_analysis)
        medicationsResult.text = getString(R.string.medications_analysis)
        chronicResult.text = getString(R.string.chronic_analysis)
        recommendationsText.text = getString(R.string.recommendations_placeholder)
    }

    private fun setupClickListeners() {
        btnNewSurvey.setOnClickListener {
            startActivity(Intent(this, HabitsTrackingActivity::class.java))
        }

        btnShareResults.setOnClickListener {
            shareResults()
        }

        btnViewHistory.setOnClickListener {
            startActivity(Intent(this, SurveyHistoryActivity::class.java))
        }

        btnGoToMenu.setOnClickListener {
            // Ir al menú principal (HomeActivity)
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun shareResults() {
        val shareText = buildShareText()
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "Resultados de Evaluación de Salud Renal")
        }

        if (shareIntent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(shareIntent, "Compartir resultados"))
        } else {
            Toast.makeText(this, "No hay aplicaciones disponibles para compartir", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildShareText(): String {
        val userName = sharedPreferences.getString("userName", "Usuario") ?: "Usuario"
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        return """
            RESULTADOS DE EVALUACIÓN DE SALUD RENAL
            Usuario: $userName
            Fecha: $currentDate

            ${riskLevel.text}

            ANÁLISIS DE SÍNTOMAS:
            ${edemaResult.text}
            ${urineResult.text}
            ${fatigueResult.text}
            ${infectionsResult.text}
            ${bloodPressureResult.text}

            ANÁLISIS DE HÁBITOS:
            ${waterResult.text}
            ${saltResult.text}
            ${exerciseResult.text}
            ${medicationsResult.text}
            ${chronicResult.text}

            RECOMENDACIONES:
            ${recommendationsText.text}

            ---
            Generado por la app Síndrome Nefrítico
            Esta evaluación es informativa y no reemplaza una consulta médica.
        """.trimIndent()
    }
}