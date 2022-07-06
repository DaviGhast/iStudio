package com.uninsubria.istudio.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uninsubria.istudio.R
import com.uninsubria.istudio.models.User
import com.uninsubria.istudio.ui.HomeActivity
import kotlinx.android.synthetic.main.fragment_first.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                HomeActivity.user = dataSnapshot.getValue(User::class.java)
                view.username_textview.text = HomeActivity.user?.name

                if (!HomeActivity.user?.profileImageUrl?.isEmpty()!!) {
                    val requestOptions = RequestOptions().placeholder(R.drawable.no_image2)

                    Glide.with(view.imageview_profile.context)
                        .load(HomeActivity.user?.profileImageUrl)
                        .apply(requestOptions)
                        .into(view.imageview_profile)

                }
            }

        })


        return view
    }


}
