package com.example.gsc.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gsc.Adapters.PastHelpAdapter
import com.example.gsc.DataClass.HelpActivityDataClass
import com.example.gsc.DataClass.PastHelpData
import com.example.gsc.R
import com.example.gsc.onBoarding.LoginScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    private lateinit var db:FirebaseFirestore
    private lateinit var recyclerView:RecyclerView
    private lateinit var mAuth:FirebaseAuth
    private lateinit var Adapter:PastHelpAdapter
    private lateinit var data:ArrayList<PastHelpData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        recyclerView=view.findViewById(R.id.rv_pastHelp)
        data=ArrayList<PastHelpData>()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth= FirebaseAuth.getInstance()
        fetchData()
        val currentUser=mAuth.currentUser
        view.findViewById<TextView>(R.id.profile_name_tv).text= currentUser?.displayName
        view.findViewById<TextView>(R.id.profile_email_tv).text=currentUser?.email
        view.findViewById<TextView>(R.id.profile_contactNo_tv).text=currentUser?.phoneNumber
        val tc=view.findViewById<ImageView>(R.id.profile_image_iv)

        Glide.with(this)
            .load(currentUser?.photoUrl)
            .into(tc)

        view.findViewById<Button>(R.id.profile_signOut).setOnClickListener {
            mAuth.signOut()
            startActivity(Intent(activity, LoginScreen::class.java))
            requireActivity().finish()
            Toast.makeText(activity,"SignOut Successful",Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchData() {
        recyclerView.layoutManager=LinearLayoutManager(requireContext())
        Adapter=PastHelpAdapter(data)
        recyclerView.adapter=Adapter
        val user=mAuth.currentUser
        db=FirebaseFirestore.getInstance()
        val locationRef=db.collection("Saves")
        val query=locationRef.whereEqualTo("user",user?.uid)
        query.get().addOnSuccessListener {documents->
            for(document in documents){
                data.add(document.toObject(PastHelpData::class.java))
            }
            Log.d("Size",data.size.toString())
            Adapter.notifyDataSetChanged()
        }

    }


}
