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
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*
import kotlinx.android.synthetic.main.post_row.view.*


class LatestMessageRow(val chatMessage: ChatMessage, val context: Context) : Item<ViewHolder>() {

    var chatPartnerUser: User? = null

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.latest_message_textview.text = chatMessage.text

        val chatPartnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.username_textview_latest_message.text = chatPartnerUser?.name
                viewHolder.itemView.latest_msg_time.text = DateUtils.getFormattedTime(chatMessage.timestamp)

                val requestOptions = RequestOptions().placeholder(R.drawable.no_image2)

                if (!chatPartnerUser?.profileImageUrl?.isEmpty()!!) {

                    Glide.with(viewHolder.itemView.imageview_latest_message.context)
                        .load(chatPartnerUser?.profileImageUrl)
                        .apply(requestOptions)
                        .into(viewHolder.itemView.imageview_latest_message)

                } else {

                    Glide.with(viewHolder.itemView.imageview_latest_message.context)
                        .load(R.drawable.no_image2)
                        .apply(requestOptions)
                        .into(viewHolder.itemView.imageview_latest_message)

                }

                viewHolder.itemView.imageview_latest_message.setOnClickListener {
                    BigImageDialog.newInstance(chatPartnerUser?.profileImageUrl!!).show((context as Activity).fragmentManager
                        , "")
                }

            }

        })


    }

}