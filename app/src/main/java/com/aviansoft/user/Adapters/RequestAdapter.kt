package com.aviansoft.user.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aviansoft.caronphone.Utils.Pref
import com.aviansoft.user.FriendUserModel
import com.aviansoft.user.databinding.ItemReqUserBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RequestAdapter(var requestList: ArrayList<FriendUserModel>) :
    Adapter<RequestAdapter.RequestHolder>() {

    lateinit var dbRef: DatabaseReference
    lateinit var pref: Pref
    lateinit var context: Context

    class RequestHolder(itemView: ItemReqUserBinding) : ViewHolder(itemView.root) {
        var binding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestHolder {
        context = parent.context
        var binding = ItemReqUserBinding.inflate(LayoutInflater.from(parent.context))
        return RequestHolder(binding)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: RequestHolder, position: Int) {

        pref = Pref(context, "Login")
        dbRef = FirebaseDatabase.getInstance().reference

        requestList.get(position).apply {
            holder.binding.apply {

                txtEmail.text = email
                txtName.text = name

                btnAccept.setOnClickListener {

                    dbRef.root.child("Request").child(uid).child("Send").child(pref.getPrefString("uid")).removeValue()
                    dbRef.root.child("Request").child(pref.getPrefString("uid")).child("Receive").child(uid).removeValue()
                    var fModel = FriendUserModel(pref.getPrefString("uid"),pref.getPrefString("email"),pref.getPrefString("name"))
                    dbRef.root.child("Friend").child(uid).child(pref.getPrefString("uid")).setValue(fModel)


                }
                btnReject.setOnClickListener {

                    dbRef.root.child("Request").child(uid).child("Send").child(pref.getPrefString("uid")).removeValue()
                    dbRef.root.child("Request").child(pref.getPrefString("uid")).child("Receive").child(uid).removeValue()


                }



            }

        }

    }

}