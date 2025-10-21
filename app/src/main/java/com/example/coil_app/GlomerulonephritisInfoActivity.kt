package com.example.coil_app

import android.os.Bundle
import androidx.appcompat.widget.Toolbar


class GlomerulonephritisInfoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glomerulonephritis_info)

        // Configurar toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.postinfectious_glomerulonephritis)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}