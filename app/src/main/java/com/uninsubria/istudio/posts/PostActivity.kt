package com.uninsubria.istudio.posts

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.uninsubria.istudio.R
import com.uninsubria.istudio.models.Post
import kotlinx.android.synthetic.main.activity_post.*


class PostActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)

        val array = intent.getParcelableArrayExtra("USER_KEY")
        val mypost: Post = array?.get(1) as Post
        titleap.text = mypost.title
        textap.text = mypost.text

        val latitude = mypost.latitude
        val longitude = mypost.longitude

        if (latitude==""||longitude==""){
            maplink.isVisible = !maplink.isVisible
        }
        else{}

        maplink.setOnClickListener(){
            val gmmIntentUri = Uri.parse("geo:$latitude,$longitude")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            mapIntent.resolveActivity(packageManager)?.let { startActivity(mapIntent) }
        }
    }


}