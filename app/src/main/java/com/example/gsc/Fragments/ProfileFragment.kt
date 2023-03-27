package com.example.gsc.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gsc.R
import com.example.gsc.onBoarding.LoginScreen
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private lateinit var recyclerView:RecyclerView
    private lateinit var mAuth:FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        recyclerView=view.findViewById(R.id.rv_pastHelp)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth= FirebaseAuth.getInstance()



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
            Toast.makeText(activity,"SignOut Successful",Toast.LENGTH_SHORT).show()
        }
    }


}