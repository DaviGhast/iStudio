package com.uninsubria.istudio.posts

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import com.uninsubria.istudio.R
import com.uninsubria.istudio.messages.NewMessageActivity
import com.uninsubria.istudio.models.Post
import com.uninsubria.istudio.models.User
import com.uninsubria.istudio.ui.HomeActivity
import com.uninsubria.istudio.ui.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_new_post.*

class NewPostActivity: AppCompatActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var longitude:String
    private lateinit var latitude:String

    private val currentUser: User?
        get() = intent.getParcelableExtra("USER_KEY")

    fun delete(){
        ettitle.text.clear()
        ettext.text.clear()
        post_checkBox.isChecked = false
    }
    private fun getCurrentLocation(){
        if(checkPermissions())
        {
            if (isLocationEnabled())
            {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){task->
                    val location: Location?=task.result
                    if (location==null){
                        Toast.makeText(this, "Null Received",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this, "Get Success",Toast.LENGTH_SHORT).show()
                        latitude=""+location.latitude
                        longitude=""+location.longitude
                    }
                }

            }
            else
            {
                Toast.makeText(this,"Abilita la localizzazione",Toast.LENGTH_SHORT).show()
                val intent=Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        else
        {
            requestPermission()
        }


    }
    private fun  isLocationEnabled():Boolean{
        val locationManager:LocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
        PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    companion object{
        private const val PERMISSION_REQUEST_ACCESS_LOCATION=100
    }
    private fun checkPermissions():Boolean{
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            ==PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
            (this, android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode== PERMISSION_REQUEST_ACCESS_LOCATION){
            if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(applicationContext,"Accesso garantito",Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }
            else{
                Toast.makeText(applicationContext,"Accesso negato",Toast.LENGTH_SHORT).show()
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        latitude=""
        longitude=""

        post_checkBox.setOnClickListener(){
            if (post_checkBox.isChecked){
                getCurrentLocation()
            }
            else {
                latitude=""
                longitude=""
            }
        }

        buttonDelete.setOnClickListener(){
            delete()
            val intent = Intent(this, HomeActivity::class.java)
            val bundle = Bundle()
            bundle.putInt("fragmentIndex", 2)
            startActivity(intent)
            overridePendingTransition(R.anim.enter, R.anim.exit)
        }

        buttonSend.setOnClickListener(){
            val db = FirebaseDatabase.getInstance("https://istudio-658b2-default-rtdb.europe-west1.firebasedatabase.app")
            val key = db.getReference("/posts").push().key
            val ref = db.getReference("/posts/$key")
            var post= currentUser?.let { it1 ->
                Post(
                    key.toString(),ettitle.text.toString(),ettext.text.toString(), it1.uid,System.currentTimeMillis()/1000,latitude,longitude)
            }
            ref.setValue(post).addOnSuccessListener {
                Log.d(RegisterActivity.TAG, "Finally we saved the post to Firebase Database")
                Toast.makeText(this, "Finally we saved the post to Firebase Database", Toast.LENGTH_SHORT).show()
                delete()
            }
                .addOnFailureListener {
                    Log.d(RegisterActivity.TAG, "Failed to set value to database: ${it.message}")
                }
            val intent = Intent(this, HomeActivity::class.java)
            val bundle = Bundle()
            bundle.putInt("fragmentIndex", 2)
            startActivity(intent)
            overridePendingTransition(R.anim.enter, R.anim.exit)
        }

    }
}