package com.aviansoft.user

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.aviansoft.caronphone.Utils.Pref
import com.aviansoft.user.Activitys.RequestActivity
import com.aviansoft.user.Adapters.FriendAdapter
import com.aviansoft.user.Adapters.RequestAdapter
import com.aviansoft.user.databinding.ActivityLoginBinding
import com.aviansoft.user.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"
    lateinit var pref: Pref
    lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = Pref(this, "Login")
        dbRef = FirebaseDatabase.getInstance().reference

        getLastLocation(pref.getPrefString("email"),pref.getPrefString("uid"))

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this,RequestActivity::class.java))
        }


        startService(Intent(this,LocationForegroundService::class.java))


        dbRef.root.child("Friend").child(pref.getPrefString("uid")).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var requestList = ArrayList<FriendUserModel>()

                for (snap in snapshot.children) {
                    var data = snap.getValue(FriendUserModel::class.java)
                    requestList.add(data!!)
                }

                binding.rcvFriendList.layoutManager = LinearLayoutManager(this@MainActivity)
                binding.rcvFriendList.adapter = FriendAdapter(requestList)


            }

            override fun onCancelled(error: DatabaseError) {
            }
        })



    }


    private fun getLastLocation(email: String, uid: String) {
        var fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    // Do something with latitude and longitude
                    Log.e(TAG, "onLocationChanged: $latitude $longitude" )
                    var lo = LocLatLong(latitude,longitude)
                    var da = UserModel(uid,email,pref.getPrefString("name"),lo)
                    dbRef.root.child("User").child(uid).setValue(da).addOnFailureListener {
                        Log.e(TAG, "onLocationChanged: ${it.message}", )
                    }
                    println("Latitude: $latitude, Longitude: $longitude")
                }
            }
    }


}