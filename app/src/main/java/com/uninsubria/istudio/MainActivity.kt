package com.uninsubria.istudio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.android.synthetic.main.signup_layout.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val datipassati: Bundle? = getIntent().getExtras()
        val dato1 = datipassati?.getString("loginemail")
        val dato2 = datipassati?.getString("signupemail")
        tvuser.setSelected(true)
        var user: String?
        tvuser.text=if(dato1.isNullOrBlank()) dato2 else dato1
    }
}