package com.example.coil_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast


class HabitsTrackingActivity : BaseActivity() {

    private lateinit var btnContinue: Button
    private lateinit var rgEdema: RadioGroup
    private lateinit var rgUrine: RadioGroup
    private lateinit var rgFatigue: RadioGroup
    private lateinit var rgInfections: RadioGroup
    private lateinit var rgBloodPressure: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habits_tracking)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        btnContinue = findViewById(R.id.btn_continue_survey)
        rgEdema = findViewById(R.id.rg_edema)
        rgUrine = findViewById(R.id.rg_urine)
        rgFatigue = findViewById(R.id.rg_fatigue)
        rgInfections = findViewById(R.id.rg_infections)
        rgBloodPressure = findViewById(R.id.rg_blood_pressure)
    }

    private fun setupListeners() {
        btnContinue.setOnClickListener {
            if (validatePart1()) {
                // Guardar respuestas de la parte 1 (podríamos usar SharedPreferences o pasar como extras)
                savePart1Answers()

                // Ir a la parte 2
                val intent = Intent(this, HabitsTrackingPart2Activity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Por favor responde todas las preguntas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validatePart1(): Boolean {
        return rgEdema.checkedRadioButtonId != -1 &&
                rgUrine.checkedRadioButtonId != -1 &&
                rgFatigue.checkedRadioButtonId != -1 &&
                rgInfections.checkedRadioButtonId != -1 &&
                rgBloodPressure.checkedRadioButtonId != -1
    }

    private fun savePart1Answers() {
        val prefs = getSharedPreferences("SurveyAnswers", MODE_PRIVATE)
        val editor = prefs.edit()

        // Guardar IDs de radio buttons para referencia
        editor.putInt("edema", rgEdema.checkedRadioButtonId)
        editor.putInt("urine", rgUrine.checkedRadioButtonId)
        editor.putInt("fatigue", rgFatigue.checkedRadioButtonId)
        editor.putInt("infections", rgInfections.checkedRadioButtonId)
        editor.putInt("bloodPressure", rgBloodPressure.checkedRadioButtonId)

        // Guardar respuestas como texto para fácil acceso
        editor.putString("q1_edema", getRadioText(rgEdema.checkedRadioButtonId))
        editor.putString("q2_urine", getRadioText(rgUrine.checkedRadioButtonId))
        editor.putString("q3_fatigue", getRadioText(rgFatigue.checkedRadioButtonId))
        editor.putString("q4_recent_infections", getRadioText(rgInfections.checkedRadioButtonId))
        editor.putString("q5_blood_pressure", getRadioText(rgBloodPressure.checkedRadioButtonId))

        editor.apply()
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

            else -> "Respuesta seleccionada"
        }
    }
}