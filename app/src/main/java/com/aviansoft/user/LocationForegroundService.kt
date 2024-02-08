package com.aviansoft.user

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.aviansoft.caronphone.Utils.Pref
import com.google.android.gms.location.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LocationForegroundService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val TAG = "LocationForegroundServi"
    private lateinit var locationCallback: LocationCallback
    lateinit var pref: Pref
    lateinit var dbRef: DatabaseReference

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        return START_STICKY
    }

    @SuppressLint("ForegroundServiceType")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForegroundService() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        pref = Pref(this, "Login")
        dbRef = FirebaseDatabase.getInstance().reference

        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("location_service", "Location Service")
            } else {
                ""
            }

        val notificationBuilder = Notification.Builder(this, channelId)
            .setContentTitle("Location Service")
            .setContentText("Fetching location...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)

        startForeground(101, notificationBuilder.build())

        val locationRequest = LocationRequest().apply {
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Handle location updates here
                    Log.e(TAG,"Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                    var lo = LocLatLong(location.latitude,location.longitude)
                    var da = UserModel(pref.getPrefString("uid"),"email","Harsh",lo)
                    dbRef.root.child("User").child(pref.getPrefString("uid")).setValue(da)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            stopSelf()
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    override fun onDestroy() {
        super.onDestroy()
//        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
            channel.lightColor = getColor(R.color.white)
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val service = getSystemService(NotificationManager::class.java)
            service.createNotificationChannel(channel)
            channelId
        } else {
            ""
        }
        return chan
    }
}