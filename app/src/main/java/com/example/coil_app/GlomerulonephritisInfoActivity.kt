package com.example.coil_app

import android.os.Bundle


class GlomerulonephritisInfoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glomerulonephritis_info)

        // Configurar toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.postinfectious_glomerulonephritis)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}