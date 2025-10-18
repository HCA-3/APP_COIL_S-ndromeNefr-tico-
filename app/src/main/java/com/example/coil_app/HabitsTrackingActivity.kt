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
        // Temporal: mapeo basado en posición hasta que los IDs se generen correctamente
        return when (checkedId) {
            // Edema (primer RadioGroup)
            2131296344 -> "No"         // rb_no_edema
            2131296345 -> "Leve"       // rb_mild_edema
            2131296346 -> "Moderado/a"  // rb_moderate_edema
            2131296347 -> "Severo/a"    // rb_severe_edema

            // Orina (segundo RadioGroup)
            2131296348 -> "Normal"                    // rb_normal_urine
            2131296349 -> "Orina oscura/color té"     // rb_dark_urine
            2131296350 -> "Con sangre"                // rb_bloody
            2131296351 -> "Espumosa"                  // rb_foamy

            // Fatiga (tercer RadioGroup)
            2131296352 -> "No"         // rb_no_fatigue
            2131296353 -> "Leve"       // rb_mild_fatigue
            2131296354 -> "Moderado/a"  // rb_moderate_fatigue
            2131296355 -> "Severo/a"    // rb_severe_fatigue

            // Infecciones (cuarto RadioGroup)
            2131296356 -> "No"                     // rb_no_infections
            2131296357 -> "Infección de garganta"  // rb_throat_infection
            2131296358 -> "Infección de piel"      // rb_skin_infection
            2131296359 -> "Otra"                   // rb_other_infection

            // Presión arterial (quinto RadioGroup)
            2131296360 -> "Normal"      // rb_normal_bp
            2131296361 -> "Alta"        // rb_high_bp
            2131296362 -> "No lo sé"    // rb_dont_know_bp

            else -> "Respuesta seleccionada"
        }
    }
}