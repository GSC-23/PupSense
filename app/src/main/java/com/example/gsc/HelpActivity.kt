package com.example.gsc

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide

class HelpActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        val latitude=intent.getDoubleExtra("latitude",0.0)
        val longitude = intent.getDoubleExtra("longitude",0.0)

        val imageView=findViewById<ImageView>(R.id.iv_map)
        Glide.with(this)
            .load(getStaticMapUrl(latitude,longitude))
            .into(imageView)
    }

    private fun getStaticMapUrl(latitude: Double, longitude: Double): String {
        val apiKey = "AIzaSyAoFVFGd4v0q28Yx-WdreQTelZt2MZCmuQ" // Replace with your API key
        val width = Resources.getSystem().displayMetrics.widthPixels
        val size = "200x${width}" // Set the size of the image
        val color = "#FFB3C1"

        return "https://maps.googleapis.com/maps/api/staticmap?center=$latitude,$longitude&zoom=15&size=$size&key=$apiKey&markers=icon:https://appurl.io/DtWgrrStFQ%7Ccolor:blue%7Clabel:S%7C$latitude,$longitude%7C"
    }
}