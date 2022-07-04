package com.uninsubria.istudio.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.uninsubria.istudio.R
import com.uninsubria.istudio.messages.ChatLogActivity
import com.uninsubria.istudio.messages.LatestMessagesActivity
import com.uninsubria.istudio.messages.NewMessageActivity
import com.uninsubria.istudio.messages.NewMessageActivity.Companion.USER_KEY
import com.uninsubria.istudio.models.ChatMessage
import com.uninsubria.istudio.models.User
import com.uninsubria.istudio.ui.register.RegisterActivity
import com.uninsubria.istudio.views.LatestMessageRow
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.fragment_latest_messages.view.*

/**
 * A simple [Fragment] subclass.
 */
class ThirdFragment : Fragment(), View.OnClickListener {


    lateinit var navController: NavController

    private val adapter = GroupAdapter<ViewHolder>()
    private val latestMessagesMap = HashMap<String, ChatMessage>()


    companion object {
        var currentUser: User? = null
        val TAG = LatestMessagesActivity::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_latest_messages, container, false)

        view.recyclerview_latest_messages.layoutManager = LinearLayoutManager(activity)
        view.recyclerview_latest_messages.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        verifyUserIsLoggedIn()

        //recyclerview_latest_messages.adapter = adapter

        swiperefresh.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.colorAccent))

        fetchCurrentUser()
        listenForLatestMessages()

        adapter.setOnItemClickListener { item, _ ->
            activity?.let{
                val intent = Intent (it, ChatLogActivity::class.java)
                val row = item as LatestMessageRow
                intent.putExtra(USER_KEY, row.chatPartnerUser)
                it.startActivity(intent)
            }
            //val intent = Intent(this, ChatLogActivity::class.java)
            //val row = item as LatestMessageRow
            //intent.putExtra(USER_KEY, row.chatPartnerUser)
            //startActivity(intent)
        }


        new_message_fab.setOnClickListener {
            activity?.let{
                val intent = Intent (it, NewMessageActivity::class.java)
                it.startActivity(intent)
            }
            //val intent = Intent(this, NewMessageActivity::class.java)
            //startActivity(intent)
        }

        view.swiperefresh.setOnRefreshListener {
            verifyUserIsLoggedIn()
            fetchCurrentUser()
            listenForLatestMessages()
        }
        //view.findViewById<FloatingActionButton>(R.id.new_message_fab).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            //R.id.new_message_fab -> navController!!.navigate(R.id.action_thirdFragment_to_chat_log)
        }
    }

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it, LatestMessagesActivity()))
        }
        swiperefresh.isRefreshing = false
    }

    private fun listenForLatestMessages() {
        swiperefresh.isRefreshing = true
        val fromId = FirebaseAuth.getInstance().uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(LatestMessagesActivity.TAG, "database error: " + databaseError.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(LatestMessagesActivity.TAG, "has children: " + dataSnapshot.hasChildren())
                if (!dataSnapshot.hasChildren()) {
                    swiperefresh.isRefreshing = false
                }
            }

        })


        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                dataSnapshot.getValue(ChatMessage::class.java)?.let {
                    latestMessagesMap[dataSnapshot.key!!] = it
                    refreshRecyclerViewMessages()
                }
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                dataSnapshot.getValue(ChatMessage::class.java)?.let {
                    latestMessagesMap[dataSnapshot.key!!] = it
                    refreshRecyclerViewMessages()
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                LatestMessagesActivity.currentUser = dataSnapshot.getValue(User::class.java)
            }

        })
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            activity?.let{
                val intent = Intent (it, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                it.startActivity(intent)
            }
            //val intent = Intent(this, RegisterActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            //startActivity(intent)
        }
    }

}
