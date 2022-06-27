package com.uninsubria.istudio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.android.synthetic.main.signup_layout.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_layout)
        auth = FirebaseAuth.getInstance()


    fun signup(view: View) {
        val email = regEmail.text.toString()
        val password = regPassword.text.toString()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Successfully registered :)", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error registering, try again later :(", Toast.LENGTH_LONG)
                    .show()
            }

        }
    }

        signUpButton.setOnClickListener(){
            signup(signUpButton)
        }

        tvLogin.setOnClickListener() {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}