package com.uninsubria.istudio.posts

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.uninsubria.istudio.R
import com.uninsubria.istudio.models.Post
import com.uninsubria.istudio.models.User
import com.uninsubria.istudio.ui.HomeActivity
import com.uninsubria.istudio.ui.fragments.SecondFragment
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.net.URI


class PostActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)

        val array = intent.getParcelableArrayExtra("USER_KEY")
        val mypost: Post = array?.get(1) as Post
        val myuser: User = array?.get(0) as User

        titleap.text = mypost.title
        textap.text = mypost.text
        postusername.text = myuser.name
        maplink.text= "See ${myuser.name} position"

        if (myuser.profileImageUrl?.isEmpty()!!) {
            val requestOptions = RequestOptions().placeholder(R.drawable.no_image2)
            Glide.with(this)
                .load(myuser.profileImageUrl)
                .apply(requestOptions)
                .into(userprofileimage)
        }
        else {
            Glide.with(this)
            .load(myuser.profileImageUrl)
            .into(userprofileimage)
        }


        val latitude = mypost.latitude
        val longitude = mypost.longitude


        if (latitude==""||longitude==""){
            maplink.isVisible = !maplink.isVisible
        }
        else{}


        maplink.setOnClickListener(){
            val gmmIntentUri = Uri.parse("geo:0,0?q=$latitude,$longitude")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            mapIntent.resolveActivity(packageManager)?.let { startActivity(mapIntent) }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val bundle = Bundle()
        bundle.putInt("fragmentIndex", 2)
        finish()
        return true
    }

}