package com.example.gsc

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreen:AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        if (mAuth.currentUser != null) {
            Handler().postDelayed(
                {
                    startActivity(Intent(this, MainActivity::class.java))
                }, 2800
            )
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