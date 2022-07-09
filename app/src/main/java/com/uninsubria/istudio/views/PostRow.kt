package com.uninsubria.istudio.views

import android.app.Activity
import android.content.Context
import com.uninsubria.istudio.R
import com.uninsubria.istudio.models.User
import com.uninsubria.istudio.utils.DateUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uninsubria.istudio.models.Post
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.post_row.view.*



class PostRow(val post: Post, val context: Context) : Item<ViewHolder>() {

    var postCreatorUser: User? = null

    override fun getLayout(): Int {
        return R.layout.post_row
    }


    override fun bind(viewHolder: ViewHolder, position: Int) {
        val postCreatorId: String = post.fromId

        val ref = FirebaseDatabase.getInstance().getReference("/users/$postCreatorId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                postCreatorUser = p0.getValue(User::class.java)
                viewHolder.itemView.post_title.text = post.title
                viewHolder.itemView.post_text.text = post.text
                viewHolder.itemView.post_time.text = DateUtils.getFormattedTime(post.timestamp)

                if (!postCreatorUser?.profileImageUrl?.isEmpty()!!) {
                    val requestOptions = RequestOptions().placeholder(R.drawable.no_image2)

                    Glide.with(viewHolder.itemView.imageview_post.context)
                        .load(postCreatorUser?.profileImageUrl)
                        .apply(requestOptions)
                        .into(viewHolder.itemView.imageview_post)

                    viewHolder.itemView.imageview_post.setOnClickListener {
                        BigImageDialog.newInstance(postCreatorUser?.profileImageUrl!!).show((context as Activity).fragmentManager
                            , "")
                    }

                }
            }

        })


    }

}