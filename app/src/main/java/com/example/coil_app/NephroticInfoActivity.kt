package com.example.coil_app

import android.os.Bundle


class NephroticInfoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nephrotic_info)

        // Configurar toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.nephrotic_syndrome)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}