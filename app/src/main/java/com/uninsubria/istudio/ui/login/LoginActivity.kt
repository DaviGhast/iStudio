package com.uninsubria.istudio.ui.login

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.uninsubria.istudio.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.uninsubria.istudio.MainActivity
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.abs_layout)
        supportActionBar!!.elevation = 0.0f

        Glide.with(this)
            .load(R.drawable.logo512x512)
            .apply(RequestOptions.circleCropTransform())
            .into(kotlinImageView)

        login_button_login.setOnClickListener {
            performLogin()
        }

        back_to_register_textview.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }

    private fun performLogin() {
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this,"Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        back_to_register_textview.visibility = View.GONE
        loading_view.visibility = View.VISIBLE

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "Successfully logged in: ${it.result!!.user?.uid}")

                } else {
                    Log.d(ContentValues.TAG, "signInWithEmail:failure", it.exception)
                    Toast.makeText(
                        this, "Authentication failed: ${it.exception!!.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                //val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(R.anim.enter, R.anim.exit)
            }
            .addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()

                back_to_register_textview.visibility = View.VISIBLE
                loading_view.visibility = View.GONE
            }
    }
}