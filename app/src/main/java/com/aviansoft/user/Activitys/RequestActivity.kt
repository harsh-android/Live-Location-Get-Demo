package com.aviansoft.user.Activitys

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.aviansoft.caronphone.Utils.Pref
import com.aviansoft.user.Adapters.RequestAdapter
import com.aviansoft.user.FriendUserModel
import com.aviansoft.user.UserModel
import com.aviansoft.user.databinding.ActivityRequestBinding
import com.aviansoft.user.databinding.DialogAddFriendBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RequestActivity : AppCompatActivity() {

    lateinit var binding : ActivityRequestBinding
    lateinit var dbRef : DatabaseReference
    lateinit var pref: Pref
    private val TAG = "RequestActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = Pref(this, "Login")
        dbRef = FirebaseDatabase.getInstance().reference

        binding.btnAddFriend.setOnClickListener {

            var dialog = Dialog(this)
            var dBinding = DialogAddFriendBinding.inflate(layoutInflater)
            dialog.setContentView(dBinding.root)

            dBinding.btnAdd.setOnClickListener {
                var email = dBinding.edtEmail.text.toString()

                dbRef.root.child("User").addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (snap in snapshot.children) {
                            var data:UserModel = snap.getValue(UserModel::class.java)!!
                            Log.e(TAG, "onDataChange: ${data.email}" )
                            if (data.email.equals(email)) {
                                var fModel = FriendUserModel(data.uid,data.email,data.name)
                                dbRef.root.child("Request").child(pref.getPrefString("uid")).child("Send").child(data.uid).setValue(fModel)
                                var uModel = FriendUserModel(pref.getPrefString("uid"),pref.getPrefString("email"),pref.getPrefString("name"))
                                dbRef.root.child("Request").child(data.uid).child("Receive").child(pref.getPrefString("uid")).setValue(uModel)
                            }
                        }
                        Log.e(TAG, "onDataChange: ---------- " )


                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "onCancelled: Faillllll == ${error.message}" )
                    }

                })

                dialog.dismiss()
            }

            dialog.show()

        }

        dbRef.root.child("Request").child(pref.getPrefString("uid")).child("Receive").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                var requestList = ArrayList<FriendUserModel>()

                for (snap in snapshot.children) {
                    var data = snap.getValue(FriendUserModel::class.java)
                    requestList.add(data!!)
                }

                binding.rcvRequestList.layoutManager = LinearLayoutManager(this@RequestActivity)
                binding.rcvRequestList.adapter = RequestAdapter(requestList)


            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


    }
}