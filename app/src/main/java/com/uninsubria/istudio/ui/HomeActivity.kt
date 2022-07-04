package com.uninsubria.istudio.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uninsubria.istudio.R
import com.uninsubria.istudio.databinding.ActivityHomeBinding
import com.uninsubria.istudio.messages.LatestMessagesActivity
import com.uninsubria.istudio.messages.NewMessageActivity
import com.uninsubria.istudio.messages.NewMessageActivity.Companion.USER_KEY
import com.uninsubria.istudio.models.User
import com.uninsubria.istudio.ui.fragments.FirstFragment
import com.uninsubria.istudio.ui.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_main.*


class HomeActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityHomeBinding

    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.main_fragment)
        setupActionBarWithNavController(navController)
        setupSmoothBottomMenu()

        val uid = FirebaseAuth.getInstance().uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue(User::class.java)
                tvuser.text = user?.name
            }

        })

        val bundle = Bundle()
        bundle.putParcelable("user", user)
        val fragInfo = FirstFragment()
        fragInfo.arguments = bundle
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun setupSmoothBottomMenu() {
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.menu_bottom)
        val menu = popupMenu.menu
        binding.bottomBar.setupWithNavController(menu, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun fetchCurrentUser() {


    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
