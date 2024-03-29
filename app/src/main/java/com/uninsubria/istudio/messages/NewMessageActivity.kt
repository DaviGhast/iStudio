package com.uninsubria.istudio.messages

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.uninsubria.istudio.views.BigImageDialog
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {

    companion object {
        const val USER_KEY = "USER_KEY"
        private val TAG = NewMessageActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        swiperefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //supportActionBar?.setLogo(R.drawable.no_image2)

        supportActionBar?.setDisplayUseLogoEnabled(true)

        supportActionBar?.title = "Select User"

        fetchUsers()


        swiperefresh.setOnRefreshListener {
            fetchUsers()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val bundle = Bundle()
        bundle.putInt("fragmentIndex", 3)
        finish()
        return true
    }

    private fun fetchUsers() {
        swiperefresh.isRefreshing = true

        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                dataSnapshot.children.forEach {
                    Log.d(TAG, it.toString())
                    @Suppress("NestedLambdaShadowedImplicitParameter")
                    it.getValue(User::class.java)?.let {
                        if (it.uid != FirebaseAuth.getInstance().uid) {
                            adapter.add(UserItem(it, this@NewMessageActivity))
                        }
                    }
                }

                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    finish()
                }

                recyclerview_newmessage.adapter = adapter
                swiperefresh.isRefreshing = false
            }

        })
    }
}

class UserItem(val user: User, val context: Context) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_message.text = user.name

        if (!user.profileImageUrl!!.isEmpty()) {
            val requestOptions = RequestOptions().placeholder(R.drawable.no_image2)


            Glide.with(viewHolder.itemView.imageview_new_message.context)
                .load(user.profileImageUrl)
                .apply(requestOptions)
                .into(viewHolder.itemView.imageview_new_message)

            viewHolder.itemView.imageview_new_message.setOnClickListener {
                BigImageDialog.newInstance(user?.profileImageUrl!!).show((context as Activity).fragmentManager
                    , "")
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

}
