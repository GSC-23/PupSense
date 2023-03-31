package com.example.gsc.onBoarding

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.gsc.MainActivity
import com.example.gsc.Permissions.hasLocationPermission
import com.example.gsc.R
import com.example.gsc.onBoarding.carouselView.CarouselView
import com.google.firebase.auth.FirebaseAuth

class SplashScreen:AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        if (mAuth.currentUser != null) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (hasLocationPermission(this) && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Handler().postDelayed(
                    {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }, 2800
                )
            } else {
                startActivity(Intent(this, PermissionActivity::class.java))
            }
        }
        else{
            Handler().postDelayed(
                {
                    startActivity(Intent(this, CarouselView::class.java))
                    finish()
                }, 2000
            )
        }
    }
}