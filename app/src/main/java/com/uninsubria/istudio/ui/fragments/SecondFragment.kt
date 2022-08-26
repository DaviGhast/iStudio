package com.uninsubria.istudio.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.uninsubria.istudio.R
import com.uninsubria.istudio.models.Post
import com.uninsubria.istudio.models.User
import com.uninsubria.istudio.posts.NewPostActivity
import com.uninsubria.istudio.posts.PostActivity
import com.uninsubria.istudio.views.PostRow
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_second.*
import kotlinx.android.synthetic.main.fragment_second.view.*

class SecondFragment : Fragment() {

    lateinit var navController: NavController

    private val adapter = GroupAdapter<ViewHolder>()
    private val postsMap = HashMap<String, Post>()


    companion object {
        var currentUser: User? = null
        val TAG = SecondFragment::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
        val view = inflater.inflate(R.layout.fragment_second, container, false)

    view.recyclerview_bacheca_posts.layoutManager = LinearLayoutManager(activity)
        view.recyclerview_bacheca_posts.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)



        swiperefresh_bacheca.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.colorAccent))

        fetchCurrentUser()
        listenForLatestPosts()

        adapter.setOnItemClickListener { item, _ ->
            activity?.let{
                val intent = Intent (it, PostActivity::class.java)
                val row = item as PostRow
                intent.putExtra("USER_KEY", arrayOf(row.postCreatorUser,row.post))
                it.startActivity(intent)
            }

        }


        new_post.setOnClickListener {
            activity?.let{
                val intent = Intent (it, NewPostActivity::class.java)
                intent.putExtra("USER_KEY", currentUser)
                it.startActivity(intent)
            }

        }

        view.swiperefresh_bacheca.setOnRefreshListener {
            fetchCurrentUser()
            listenForLatestPosts()
        }

    }



    private fun refreshBacheca() {
        adapter.clear()
        val sortedMap: MutableMap<String, Post> = LinkedHashMap()
        postsMap.entries.sortedByDescending { it.value.timestamp }.forEach{ sortedMap[it.key] =
            it.value }
        sortedMap.values.forEach {
            adapter.add(PostRow(it, requireContext()))
        }
        swiperefresh_bacheca.isRefreshing = false
    }

    private fun listenForLatestPosts() {
        swiperefresh_bacheca.isRefreshing = true
        val ref = FirebaseDatabase.getInstance().getReference("/posts")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, "database error: " + databaseError.message)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                dataSnapshot.getValue(Post::class.java)?.let {
                    postsMap[dataSnapshot.key!!] = it
                    refreshBacheca()
                }
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                dataSnapshot.getValue(Post::class.java)?.let {
                    postsMap[dataSnapshot.key!!] = it
                    refreshBacheca()
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
                currentUser = dataSnapshot.getValue(User::class.java)
            }

        })
    }




}
