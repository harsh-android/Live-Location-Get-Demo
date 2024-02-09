package com.aviansoft.user.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aviansoft.caronphone.Utils.Pref
import com.aviansoft.user.R
import com.aviansoft.user.UserModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MapsFragment : Fragment() {

    lateinit var pref: Pref
    lateinit var dbRef: DatabaseReference
    private lateinit var placesClient: PlacesClient


    private val callback = OnMapReadyCallback { googleMap ->

        pref = Pref(requireContext(), "Login")
        dbRef = FirebaseDatabase.getInstance().reference

        var ref = 0

        var uid = arguments?.getString("uid")
        dbRef.root.child("User").child(uid!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var data = snapshot.getValue(UserModel::class.java)
                val sydney = LatLng(data?.location?.lat!!, data?.location?.long!!)

                if (ref == 0) {
                    googleMap.addMarker(MarkerOptions().position(sydney).title(data.name))
                    googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                    ref++

                }
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18f))

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Places.initialize(requireContext(), "AIzaSyDSKlX2C4tu21djqYZOdFQso35YHLGJeQw")
        placesClient = Places.createClient(requireContext())

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}