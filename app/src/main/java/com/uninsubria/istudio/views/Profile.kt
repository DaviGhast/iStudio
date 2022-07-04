package com.uninsubria.istudio.views

import android.app.Activity
import android.content.Context
import com.uninsubria.istudio.R
import com.uninsubria.istudio.models.ChatMessage
import com.uninsubria.istudio.models.User
import com.uninsubria.istudio.utils.DateUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.uninsubria.istudio.views.BigImageDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uninsubria.istudio.ui.HomeActivity
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_first.view.*
import kotlinx.android.synthetic.main.latest_message_row.view.*
import kotlinx.android.synthetic.main.profile.view.*


class Profile(val chatMessage: ChatMessage, val context: Context) : Item<ViewHolder>() {

    var user: User? = null

    override fun getLayout(): Int {
        return R.layout.profile
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                user = p0.getValue(User::class.java)
                viewHolder.itemView.username_textview.text = user?.name

                if (!user?.profileImageUrl?.isEmpty()!!) {
                    val requestOptions = RequestOptions().placeholder(R.drawable.no_image2)

                    Glide.with(viewHolder.itemView.imageview_profile.context)
                        .load(user?.profileImageUrl)
                        .apply(requestOptions)
                        .into(viewHolder.itemView.imageview_profile)

                    viewHolder.itemView.imageview_profile.setOnClickListener {
                        BigImageDialog.newInstance(user?.profileImageUrl!!).show((context as Activity).fragmentManager
                            , "")
                    }

                }
            }

        })


    }

}