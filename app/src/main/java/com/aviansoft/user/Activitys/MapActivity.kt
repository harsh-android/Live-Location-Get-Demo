package com.aviansoft.user.Activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aviansoft.user.Fragments.MapsFragment
import com.aviansoft.user.R
import com.aviansoft.user.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity() {

    lateinit var binding: ActivityMapBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var uid = intent.getStringExtra("uid")

        var fragment = MapsFragment()
        var bundle = Bundle()
        bundle.putString("uid", uid)
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.mapFrag, fragment).commit()
    }
}