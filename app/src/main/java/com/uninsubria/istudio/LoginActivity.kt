package com.uninsubria.istudio

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity: AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        auth = FirebaseAuth.getInstance()


        fun login(view: View) {
            val email = etemail.text.toString()
            val password = etpassword.text.toString()
            if (!email.isEmpty() && !password.isEmpty()) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail:success")
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("loginemail",email)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.d(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            this, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this,"Please fill up the Credentials :|", Toast.LENGTH_SHORT).show()
            }
        }

        loginbutton.setOnClickListener(){
            login(loginbutton)
        }

        tvRegister.setOnClickListener(){
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}