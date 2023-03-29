package com.example.gsc

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.gsc.Adapters.HelpRecyclerAdapter
import com.example.gsc.Constants.API_KEY
import com.example.gsc.DataClass.HelpActivityDataClass
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import com.google.rpc.Help
import kotlinx.coroutines.*
import java.lang.Math.cos
import java.net.URL

class HelpActivity : AppCompatActivity() {
    private lateinit var data :ArrayList<HelpActivityDataClass>
    private lateinit var helpRecyclerAdapter:HelpRecyclerAdapter
    private lateinit var shimmerLayout :ShimmerFrameLayout
    private lateinit var recyclerView:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        data=ArrayList<HelpActivityDataClass>()
        recyclerView=findViewById(R.id.rv_hospitals)
        shimmerLayout=findViewById(R.id.shimmer_layout)
        shimmerLayout.startShimmer()
        val latitude=intent.getDoubleExtra("latitude",3.0)
        val longitude=intent.getDoubleExtra("longitude",0.0)
        val recyclerView = findViewById<RecyclerView>(R.id.rv_hospitals)
        recyclerView.layoutManager = LinearLayoutManager(this)
        helpRecyclerAdapter= HelpRecyclerAdapter(data)
        recyclerView.adapter = helpRecyclerAdapter
        GlobalScope.launch (Dispatchers.IO){
            delay(1500)
            fetchData(latitude,longitude)
        }

    }

    private suspend fun fetchData(latitude: Double, longitude: Double) {

        val context = GeoApiContext.Builder()
            .apiKey(API_KEY)
            .build()

        if (!Places.isInitialized()) {
            Places.initialize(this, "AIzaSyBWJu44Js9xy8ZFUy1wAsxfSWmgbrtEv18")
        }

        val placesClient = Places.createClient(this)
        val radius = 5000 // in meters
        val type = "veterinary_care"
        val apiKey = "AIzaSyBWJu44Js9xy8ZFUy1wAsxfSWmgbrtEv18"

        val queue = Volley.newRequestQueue(this)
        val url =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$latitude,$longitude&radius=$radius&type=$type&key=$apiKey"


        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val JsonArray=response.getJSONArray("results")
                for (i in 0 until JsonArray.length()){
                    val JsonObject=JsonArray.getJSONObject(i)
                    val name=JsonObject.getString("name")
                    var address= ""
                    if (JsonObject.has("plus_code") && !JsonObject.isNull("plus_code")) {
                        address = JsonObject.getJSONObject("plus_code").getString("compound_code")
                    }
                    val rating=JsonObject.getString("rating")
                    val origin=com.google.maps.model.LatLng(latitude, longitude)
                    val destination=com.google.maps.model.LatLng(JsonObject.getJSONObject("geometry").getJSONObject("location").getString("lat").toDouble(),JsonObject.getJSONObject("geometry").getJSONObject("location").getString("lng").toDouble())
                            val directionsResult: DirectionsResult = DirectionsApi.newRequest(context)
                                .origin(origin)
                                .destination(destination)
                                .mode(TravelMode.DRIVING)
                                .await()
                            val polyline = directionsResult.routes[0].overviewPolyline
                            val staticMapUrl = "https://maps.googleapis.com/maps/api/staticmap" +
                                    "?size=600x400" +"&zoom=13" +
                                    "&maptype=roadmap" +
                                    "&path=enc:${polyline.encodedPath}" +
                                    "&markers=color:green|label:S|${origin}" +
                                    "&markers=color:red|label:D|${destination}" +
                                    "&key=$API_KEY"
                            data.add(HelpActivityDataClass("$name , $address",staticMapUrl,rating))
                            Log.d("url",staticMapUrl)
                    helpRecyclerAdapter.notifyDataSetChanged()
                    if (helpRecyclerAdapter.itemCount > 0) {
                        // RecyclerView is ready to display data
                        GlobalScope.launch(Dispatchers.Main) {
                            shimmerLayout.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        }
                    } else {
                        // RecyclerView is not ready to display data
                        GlobalScope.launch(Dispatchers.Main) {
                            shimmerLayout.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        }
                    }
                }
            },
            { error ->
            }
        )
        queue.add(jsonObjectRequest)
    }
    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
    }

}