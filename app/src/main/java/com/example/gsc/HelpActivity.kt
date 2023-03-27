package com.example.gsc

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import kotlin.properties.Delegates

class HelpActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        var latitude=intent.getDoubleExtra("latitude",0.0)
        var longitude = intent.getDoubleExtra("longitude",0.0)

        val imageView=findViewById<ImageView>(R.id.iv_map)
        Glide.with(this)
            .load(getStaticMapUrl(latitude,longitude))
            .into(imageView)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Retrieve the user's current location using the FusedLocationProviderClient API
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                // Construct the Places API request URL
                val radius = 10000 // in meters
                val type = "veterinary"
                val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${location?.latitude},${location?.longitude}&radius=$radius&type=$type&key=AIzaSyCpr0ZyHFFcpbnTSVD7434Oc9m01BzBhEo"

                // Send the request to the Places API using Volley or OkHttp
                val requestQueue = Volley.newRequestQueue(this)
                val request = JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    { response ->
                        // Parse the response and extract the necessary information about the nearby veterinary hospitals
                        val results = response.getJSONArray("results")
                        for (i in 0 until results.length()) {
                            val result = results.getJSONObject(i)

                            val name = result.getString("name")
                            Log.d("address1", name.toString())
                            val address = result.getString("vicinity")
                            val location = result.getJSONObject("geometry").getJSONObject("location")
                            val latitude1 = location.getDouble("lat")
                            val longitude1 = location.getDouble("lng")
                            // Do something with the information, such as adding markers to a map or displaying them in a list
                        }
                    },
                    { error ->
                        // Handle the error
                    }
                )
                requestQueue.add(request)
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }
    }

    private fun getStaticMapUrl(latitude: Double, longitude: Double): String {
        val apiKey = "AIzaSyAoFVFGd4v0q28Yx-WdreQTelZt2MZCmuQ" // Replace with your API key
        val width = Resources.getSystem().displayMetrics.widthPixels
        val size = "200x${width}" // Set the size of the image
        val color = "#FFB3C1"

        return "https://maps.googleapis.com/maps/api/staticmap?center=$latitude,$longitude&zoom=15&size=$size&key=$apiKey&markers=icon:https://appurl.io/DtWgrrStFQ%7Ccolor:blue%7Clabel:S%7C$latitude,$longitude%7C"
    }
}