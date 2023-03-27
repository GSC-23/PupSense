package com.example.gsc.onBoarding

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.gsc.MainActivity
import com.example.gsc.Permissions.hasLocationPermission
import com.example.gsc.R
import com.google.firebase.auth.FirebaseAuth

class SplashScreen:AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        if (mAuth.currentUser != null) {
            if(hasLocationPermission(this)){
            Handler().postDelayed(
                {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }, 2800
            )

        }else{
            startActivity(Intent(this,PermissionActivity::class.java))
            }
        }
        else{
            Handler().postDelayed(
                {
                    startActivity(Intent(this, LoginScreen::class.java))
                }, 2800
            )
        }
    }
}