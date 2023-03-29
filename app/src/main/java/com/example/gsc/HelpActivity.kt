package com.example.gsc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.gsc.Adapters.HelpRecyclerAdapter
import com.example.gsc.DataClass.HelpActivityDataClass
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.rpc.Help
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Math.cos

class HelpActivity : AppCompatActivity() {
    private lateinit var data :ArrayList<HelpActivityDataClass>
    private lateinit var helpRecyclerAdapter:HelpRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        data=ArrayList<HelpActivityDataClass>()
        val latitude=intent.getDoubleExtra("latitude",3.0)
        val longitude=intent.getDoubleExtra("longitude",0.0)
        val recyclerView = findViewById<RecyclerView>(R.id.rv_hospitals)
        recyclerView.layoutManager = LinearLayoutManager(this)
        helpRecyclerAdapter= HelpRecyclerAdapter(data)
        recyclerView.adapter = helpRecyclerAdapter

        fetchData(latitude,longitude)
    }

    private fun fetchData(latitude: Double, longitude: Double){

        if (!Places.isInitialized()) {
            Places.initialize(this, "AIzaSyBWJu44Js9xy8ZFUy1wAsxfSWmgbrtEv18")
        }

        val placesClient = Places.createClient(this)
        val radius = 15000 // in meters
        val type = "veterinary_care"
        val apiKey="AIzaSyBWJu44Js9xy8ZFUy1wAsxfSWmgbrtEv18"

        val queue= Volley.newRequestQueue(this)
        val url =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$latitude,$longitude&radius=$radius&type=$type&key=$apiKey"
        val jsonObjectRequest: JsonObjectRequest =JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            {
                val JsonArray=it.getJSONArray("results")
                for (i in 0 until JsonArray.length()){
                    val JsonObject=JsonArray.getJSONObject(i)
                    val name=JsonObject.getString("name")
                    var address= ""
                    if (JsonObject.has("plus_code") && !JsonObject.isNull("plus_code")) {
                        address = JsonObject.getJSONObject("plus_code").getString("compound_code")
                    }
//                    val imageUrl=newsJSONObject.getString("icon")
                    data.add(HelpActivityDataClass("$name , $address"))
                }
                helpRecyclerAdapter.notifyDataSetChanged()
            },
            {
            })
        queue.add(jsonObjectRequest)

    }

    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
    }
}