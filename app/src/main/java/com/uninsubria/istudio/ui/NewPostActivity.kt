package com.uninsubria.istudio.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.uninsubria.istudio.R
import com.uninsubria.istudio.messages.NewMessageActivity
import com.uninsubria.istudio.models.Post
import com.uninsubria.istudio.models.User
import com.uninsubria.istudio.ui.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_new_post.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.post_row.*

class NewPostActivity: AppCompatActivity() {

    private val currentUser: User?
        get() = intent.getParcelableExtra("USER_KEY")

    fun delete(){
        ettitle.text.clear()
        ettext.text.clear()
        post_checkBox.isChecked = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)


        buttonDelete.setOnClickListener(){
            delete()
        }

        buttonSend.setOnClickListener(){
            val db = FirebaseDatabase.getInstance("https://istudio-658b2-default-rtdb.europe-west1.firebasedatabase.app")
            val key = db.getReference("/posts").push().key
            val ref = db.getReference("/posts/$key")
            var post= currentUser?.let { it1 ->
                Post(
                    key.toString(),ettitle.text.toString(),ettext.text.toString(), it1.uid,System.currentTimeMillis()/1000)
            }
            ref.setValue(post).addOnSuccessListener {
                Log.d(RegisterActivity.TAG, "Finally we saved the post to Firebase Database")
                delete()
            }
                .addOnFailureListener {
                    Log.d(RegisterActivity.TAG, "Failed to set value to database: ${it.message}")
                }

        }

    }
}