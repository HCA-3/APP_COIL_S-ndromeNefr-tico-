package com.example.coil_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class SurveyHistoryActivity : BaseActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptyHistory: TextView
    private lateinit var surveyHistoryAdapter: SurveyHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_history)

        initViews()
        setupToolbar()
        loadSurveyHistory()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.recycler_view_history)
        tvEmptyHistory = findViewById(R.id.tv_empty_history)

        recyclerView.layoutManager = LinearLayoutManager(this)
        surveyHistoryAdapter = SurveyHistoryAdapter { surveyData ->
            // Al hacer clic en un elemento, mostrar detalles de esa evaluación
            showSurveyDetails(surveyData)
        }
        recyclerView.adapter = surveyHistoryAdapter
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadSurveyHistory() {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val surveyHistorySet = sharedPreferences.getStringSet("surveyHistory", emptySet())

        if (surveyHistorySet.isNullOrEmpty()) {
            tvEmptyHistory.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmptyHistory.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            try {
                // Convertir Set a List y ordenar por fecha (más reciente primero)
                val surveyList = mutableListOf<JSONObject>()

                surveyHistorySet.forEach { jsonString ->
                    try {
                        val jsonObject = JSONObject(jsonString)
                        surveyList.add(jsonObject)
                    } catch (e: Exception) {
                        // Ignorar JSON inválidos y continuar
                        android.util.Log.e("SurveyHistoryActivity", "Error parsing JSON: ${e.message}")
                    }
                }

                if (surveyList.isEmpty()) {
                    // Si todos los JSON eran inválidos, mostrar mensaje vacío
                    tvEmptyHistory.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    // Ordenar por timestamp
                    val sortedList = surveyList.sortedByDescending {
                        it.optLong("completedAt", System.currentTimeMillis())
                    }
                    surveyHistoryAdapter.updateData(sortedList)
                }
            } catch (e: Exception) {
                android.util.Log.e("SurveyHistoryActivity", "Error loading survey history: ${e.message}")
                // Mostrar mensaje vacío en caso de error
                tvEmptyHistory.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        }
    }

    private fun showSurveyDetails(surveyData: JSONObject) {
        try {
            // Guardar esta evaluación como la "última" para mostrarla en SurveyResultsActivity
            val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("lastSurveyData", surveyData.toString())
            editor.apply()

            // Ir a la pantalla de resultados
            val intent = Intent(this, SurveyResultsActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            android.util.Log.e("SurveyHistoryActivity", "Error showing survey details: ${e.message}")
            Toast.makeText(this, "Error al cargar los detalles de la evaluación", Toast.LENGTH_SHORT).show()
        }
    }
}

class SurveyHistoryAdapter(
    private val onItemClick: (JSONObject) -> Unit
) : RecyclerView.Adapter<SurveyHistoryAdapter.SurveyHistoryViewHolder>() {

    private var surveyList: List<JSONObject> = emptyList()

    fun updateData(newList: List<JSONObject>) {
        surveyList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_survey_history, parent, false)
        return SurveyHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SurveyHistoryViewHolder, position: Int) {
        holder.bind(surveyList[position])
    }

    override fun getItemCount(): Int = surveyList.size

    inner class SurveyHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tv_survey_date)
        private val tvRiskLevel: TextView = itemView.findViewById(R.id.tv_risk_level)
        private val tvSummary: TextView = itemView.findViewById(R.id.tv_survey_summary)

        fun bind(surveyData: JSONObject) {
            try {
                // Formatear fecha
                val timestamp = surveyData.optLong("completedAt", System.currentTimeMillis())
                val date = Date(timestamp)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                tvDate.text = dateFormat.format(date)

                // Calcular nivel de riesgo
                val riskScore = calculateRiskScore(surveyData)
                val riskLevel = when {
                    riskScore >= 8 -> "ALTO"
                    riskScore >= 5 -> "MODERADO"
                    riskScore >= 2 -> "BAJO"
                    else -> "MÍNIMO"
                }
                tvRiskLevel.text = "Riesgo: $riskLevel"

                // Color según nivel de riesgo
                val riskColor = when {
                    riskScore >= 8 -> "#C1121F" // Rojo
                    riskScore >= 5 -> "#FF9800" // Naranja
                    riskScore >= 2 -> "#669BBC" // Azul
                    else -> "#4CAF50" // Verde
                }
                tvRiskLevel.setTextColor(android.graphics.Color.parseColor(riskColor))

                // Resumen de síntomas principales
                val summary = buildSummary(surveyData)
                tvSummary.text = summary

                // Click listener
                itemView.setOnClickListener {
                    onItemClick(surveyData)
                }
            } catch (e: Exception) {
                android.util.Log.e("SurveyHistoryAdapter", "Error binding survey data: ${e.message}")
                // Mostrar datos por defecto en caso de error
                tvDate.text = "Fecha no disponible"
                tvRiskLevel.text = "Riesgo: Desconocido"
                tvRiskLevel.setTextColor(android.graphics.Color.GRAY)
                tvSummary.text = "Error al cargar datos"
            }
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

        private fun buildSummary(surveyData: JSONObject): String {
            val symptoms = mutableListOf<String>()

            val edema = surveyData.optString("q1_edema")
            if (edema != "No") symptoms.add("Edema: $edema")

            val urine = surveyData.optString("q2_urine")
            if (urine != "Normal") symptoms.add("Orina: $urine")

            val fatigueLevel = surveyData.optString("q3_fatigue")
            if (fatigueLevel != "No") symptoms.add("Fatiga: $fatigueLevel")

            val infections = surveyData.optString("q4_recent_infections")
            if (infections != "No") symptoms.add("Infecciones: $infections")

            val bp = surveyData.optString("q5_blood_pressure")
            if (bp == "Alta") symptoms.add("Presión: $bp")

            return if (symptoms.isNotEmpty()) {
                symptoms.take(3).joinToString(" • ") + if (symptoms.size > 3) "..." else ""
            } else {
                "Sin síntomas reportados"
            }
        }
    }
}